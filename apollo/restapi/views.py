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


class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class ServiceViewSet(viewsets.ModelViewSet):
    queryset = Service.objects.all()
    serializer_class = ServiceSerializer


class HealthCheckViewSet(viewsets.ModelViewSet):
    queryset = HealthChecks.objects.all()
    serializer_class = HealthChecksSerializer


class LabelViewSet(viewsets.ModelViewSet):
    queryset = Label.objects.all()
    serializer_class = LabelSerializer


class ConstraintViewSet(viewsets.ModelViewSet):
    queryset = Constraint.objects.all()
    serializer_class = ConstraintSerializer


class EnvironmentVariableViewSet(viewsets.ModelViewSet):
    queryset = EnvironmentVariable.objects.all()
    serializer_class = EnvironmentVariableSerializer


class VolumeViewSet(viewsets.ModelViewSet):
    queryset = Volume.objects.all()
    serializer_class = VolumeSerializer


class PortMappingViewSet(viewsets.ModelViewSet):
    queryset = PortMapping.objects.all()
    serializer_class = PortMappingSerializer


class DeploymentViewSet(viewsets.ViewSet):

    # TODO: Need to catch delete here, and send the delete to marathon in order to revert the deployment. Should delete from DB?

    def list(self, request):
        queryset = Deployment.objects.all()
        serializer = DeploymentSerializer(queryset, many=True)
        return Response(serializer.data)


    def create(self, request):
        serialized = DeploymentSerializer(data=request.data)

        if serialized.is_valid():


            # Check for permissions.. if there is a permission object related to this environment and no service
            # If there is:
            # check if the user is a member of the usergroup of the permission. if he is, continue.
            #
            # Check for permission related to this environment and this service
            # if there is:
            # check if the user is a member of the usergroup of the permission. if he is not, return 403.
            #
            # If there is no permission related to this environment or service, return 403.

            deployment = Deployment.objects.create(
                target_version=request.data['target_version'],
                deployed_service=request.data['deployed_service'],
                deployable_version=request.data['deployable_version'],
                initiated_by=request.user.id
            )
            deployment.save()

            return Response("ok", status=status.HTTP_201_CREATED)
        else:
            return  Response(serialized.errors, status=status.HTTP_400_BAD_REQUEST)

    def destroy(self, request):
        pass

    @detail_route(methods=['get'])
    def logs(self, request, pk=None):
        return Response("Aaaaass")


class EnvironmentViewSet(viewsets.ModelViewSet):
    queryset = Environment.objects.all()
    serializer_class = EnvironmentSerializer


class GroupViewSet(viewsets.ModelViewSet):
    queryset = Group.objects.all()
    serializer_class = GroupSerializer


class PermissionViewSet(viewsets.ModelViewSet):
    queryset = Permission.objects.all()
    serializer_class = PermissionSerializer


class BlockerViewSet(viewsets.ModelViewSet):
    queryset = Blocker.objects.all()
    serializer_class = BlockerSerializer


class NotificationViewSet(viewsets.ModelViewSet):
    queryset = Notification.objects.all()
    serializer_class = NotificationSerializer


class WatcherViewSet(viewsets.ModelViewSet):
    queryset = Watcher.objects.all()
    serializer_class = WatcherSerializer


class DeployableVersionViewSet(viewsets.ModelViewSet):
    queryset = DeployableVersion.objects.all()
    serializer_class = DeployableVersionSerializer


# TODO: Change from hard-coded values
class RunningDeploymentsViewSet(viewsets.ReadOnlyModelViewSet):
    time_threshold = datetime.now() - timedelta(minutes=60)
    queryset = Deployment.objects.filter((~Q(deployment_status="done-success") &
                                         ~Q(deployment_status="done-failed")) |
                                         Q(last_updated__gt=time_threshold))

    serializer_class = DeploymentSerializer


class CurrentDeploymentsView(APIView):

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

    def post(self, request):
        serialized = UserSerializer(data=request.data)

        if serialized.is_valid():
            user = User.objects.create(
                username=request.data['email'],
                email=request.data['email'],
                first_name=request.data['first_name'],
                last_name=request.data['last_name']
            )
            user.set_password(request.data['password'])
            user.save()

            return Response("ok", status=status.HTTP_201_CREATED)
        else:
            return  Response(serialized.errors, status=status.HTTP_400_BAD_REQUEST)