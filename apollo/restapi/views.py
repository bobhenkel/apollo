from rest_framework import viewsets
from serializers import *
from models import *
from django.db.models import Q
from rest_framework.decorators import detail_route
from rest_framework.response import Response
from datetime import datetime, timedelta
from rest_framework.views import APIView
from collections import defaultdict
from django.contrib.auth.models import User, Group
from rest_framework import status
from rest_framework.permissions import IsAuthenticated
from django.shortcuts import get_object_or_404
from django.db import IntegrityError

class UserViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = User.objects.all()
    serializer_class = UserSerializer


class ServiceViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Service.objects.all()
    serializer_class = ServiceSerializer


class HealthCheckViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = HealthChecks.objects.all()
    serializer_class = HealthChecksSerializer


class LabelViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Label.objects.all()
    serializer_class = LabelSerializer


class ConstraintViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Constraint.objects.all()
    serializer_class = ConstraintSerializer


class EnvironmentVariableViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = EnvironmentVariable.objects.all()
    serializer_class = EnvironmentVariableSerializer


class VolumeViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Volume.objects.all()
    serializer_class = VolumeSerializer


class PortMappingViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = PortMapping.objects.all()
    serializer_class = PortMappingSerializer


class DeploymentViewSet(viewsets.ViewSet):

    # TODO: Need to catch delete here, and send the delete to marathon in order to revert the deployment. Should delete from DB?
    permission_classes = (IsAuthenticated,)

    def list(self, request):
        queryset = Deployment.objects.all()
        serializer = DeploymentSerializer(queryset, many=True)
        return Response(serializer.data)

    def retrieve(self, request, pk=None):
        queryset = Deployment.objects.all()
        deployment = get_object_or_404(queryset, pk=pk)
        serializer = DeploymentSerializer(deployment)
        return Response(serializer.data)

    def create(self, request):
        serialized = DeploymentSerializer(data=request.data)

        if serialized.is_valid():

            target_version=request.data['target_version']
            deployed_service=request.data['deployed_service']
            deployed_environment=request.data['deployed_environment']
            deployable_version=request.data['deployable_version']
            initiated_by=request.user

            have_permissions = False
            should_enforce_permissions = False

            related_env_permissions = Permission.objects.filter(Q(environment=deployed_environment)& Q(service=None))
            all_user_permissions = initiated_by.groups.all()

            if len(related_env_permissions) > 0:
                should_enforce_permissions = True

            for perm in related_env_permissions:
                for user_perm in all_user_permissions:
                    if perm.user_group.id == user_perm.id:
                        have_permissions = True
                        break

            if not have_permissions:
                specific_permissions = Permission.objects.filter(Q(environment=deployed_environment)& Q(service=deployed_service))

                if len(specific_permissions) > 0:
                    should_enforce_permissions = True

                for perm in specific_permissions:
                    for user_perm in all_user_permissions:
                        if perm.user_group.id == user_perm.id:
                            have_permissions = True
                            break

            if should_enforce_permissions and not have_permissions:
                return Response("You have no permission to deploy this service on this environment", status=status.HTTP_403_FORBIDDEN)

            deployment = Deployment.objects.create(
                target_version=target_version,
                deployed_service_id=deployed_service,
                deployed_environment_id=deployed_environment,
                deployable_version_id=deployable_version,
                initiated_by_id=initiated_by.id
            )

            deployment.save()

            return Response(deployment.id, status=status.HTTP_201_CREATED)


        else:
            return  Response(serialized.errors, status=status.HTTP_400_BAD_REQUEST)

    def destroy(self, request):
        pass

    @detail_route(methods=['get'])
    def logs(self, request, pk=None):
        return Response("Aaaaass")


class EnvironmentViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Environment.objects.all()
    serializer_class = EnvironmentSerializer


class GroupViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Group.objects.all()
    serializer_class = GroupSerializer


class PermissionViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Permission.objects.all()
    serializer_class = PermissionSerializer


class BlockerViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Blocker.objects.all()
    serializer_class = BlockerSerializer


class NotificationViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Notification.objects.all()
    serializer_class = NotificationSerializer


class WatcherViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = Watcher.objects.all()
    serializer_class = WatcherSerializer


class DeployableVersionViewSet(viewsets.ModelViewSet):
    permission_classes = (IsAuthenticated,)
    queryset = DeployableVersion.objects.all()
    serializer_class = DeployableVersionSerializer


# TODO: Change from hard-coded values
class RunningDeploymentsViewSet(viewsets.ReadOnlyModelViewSet):
    permission_classes = (IsAuthenticated,)
    time_threshold = datetime.now() - timedelta(minutes=60)
    queryset = Deployment.objects.filter((~Q(deployment_status="done-success") &
                                         ~Q(deployment_status="done-failed")) |
                                         Q(last_updated__gt=time_threshold))

    serializer_class = DeploymentSerializer


class CurrentDeploymentsView(APIView):
    permission_classes = (IsAuthenticated,)

    def get(self, request, format=None):
        deployments = Deployment.objects.filter(deployment_status="done-success").order_by('-last_updated')

        state_dict = defaultdict(int)
        response_list = []

        # TODO: If service versions screen becomes slow, this is probably the reason.
        # TODO: It currently scans all deployment history. This probably can be done better..
        for deployment in deployments:
            if state_dict[(deployment.deployed_service, deployment.deployed_environment)] == 0:
                response_list.append(deployment)
                state_dict[(deployment.deployed_service, deployment.deployed_environment)] += 1

        return Response(DeploymentSerializer(response_list, many=True).data)


class SignUpView(APIView):
    permission_classes = (IsAuthenticated,)

    def post(self, request):
        serialized = UserSerializer(data=request.data)

        if serialized.is_valid():
            try:
                user = User.objects.create(
                    username=request.data['email'],
                    email=request.data['email'],
                    first_name=request.data['first_name'],
                    last_name=request.data['last_name']
                )
                user.set_password(request.data['password'])
                user.save()
            except IntegrityError:
                return Response("Duplicate email", status=status.HTTP_409_CONFLICT)

            return Response("ok", status=status.HTTP_201_CREATED)
        else:
            return  Response(serialized.errors, status=status.HTTP_400_BAD_REQUEST)