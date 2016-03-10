from rest_framework import serializers
from models import *


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('email', 'first_name', 'last_name', 'password', 'groups')


class ServiceSerializer(serializers.ModelSerializer):
    class Meta:
        model = Service
        fields = ('id', 'name', 'notifications', 'number_of_instances', 'marathon_id', 'cpus', 'memory',
                  'image_name', 'command', 'port_mappings', 'volumes', 'environment_variable', 'constraints',
                  'labels', 'health_checks', 'network_type', 'privileged', 'upgrade_minimum_health_capacity',
                  'upgrade_maximum_over_capacity')


class HealthChecksSerializer(serializers.ModelSerializer):
    class Meta:
        model = HealthChecks
        fields = ('protocol', 'path', 'grace_period_seconds', 'interval_seconds', 'port_index', 'port',
                  'timeout_seconds', 'max_consecutive_failures', 'command')


class LabelsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Labels
        fields = ('name', 'value')


class ConstraintsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Constraints
        fields = ('attribute', 'operator', 'value')


class EnvironmentVariableSerializer(serializers.ModelSerializer):
    class Meta:
        model = EnvironmentVariable
        fields = ('name', 'value')


class VolumesSerializer(serializers.ModelSerializer):
    class Meta:
        model = Volumes
        fields = ('container_path', 'host_path', 'mode')


class PortMappingsSerializer(serializers.ModelSerializer):
    class Meta:
        model = PortMappings
        fields = ('container_port', 'host_port', 'service_port')


class DeploymentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Deployment
        fields = ('marathon_id', 'deployed_service', 'target_version', 'source_version', 'initiated_by')


class EnvironmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Environment
        fields = ('name', 'geo_region', 'availability', 'marathon_master')


class GroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = Group
        fields = 'name'


class PermissionsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Permission
        fields = ('service', 'user_group', 'can_deploy')


class BlockerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Blocker
        fields = ('blocked_service', 'blocked_environment', 'blocked_user_group', 'description', 'created_by')


class NotificationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Notification
        fields = ('name','mailing_list','slack_channel', 'send_on')


class WatcherSerializer(serializers.ModelSerializer):
    class Meta:
        model = Watcher
        fields = ('user', 'object_type', 'object_id', 'notifications')