from rest_framework import viewsets
from serializers import *
from models import *
from django.db.models import Q
from rest_framework.decorators import detail_route
from rest_framework.response import Response
from datetime import datetime, timedelta

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


class DeploymentViewSet(viewsets.ModelViewSet):
    queryset = Deployment.objects.all()
    serializer_class = DeploymentSerializer
    # TODO: Need to catch delete here, and send the delete to marathon in order to revert the deployment. Should delete from DB?

    @detail_route()
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