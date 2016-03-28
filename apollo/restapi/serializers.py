from rest_framework import serializers
from models import *


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'email', 'first_name', 'last_name', 'password', 'groups')


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
        fields = ('id', 'protocol', 'path', 'grace_period_seconds', 'interval_seconds', 'port_index', 'port',
                  'timeout_seconds', 'max_consecutive_failures', 'command')


class LabelSerializer(serializers.ModelSerializer):
    class Meta:
        model = Label
        fields = ('id', 'name', 'value')


class ConstraintSerializer(serializers.ModelSerializer):
    class Meta:
        model = Constraint
        fields = ('id', 'attribute', 'operator', 'value')


class EnvironmentVariableSerializer(serializers.ModelSerializer):
    class Meta:
        model = EnvironmentVariable
        fields = ('id', 'name', 'value')


class VolumeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Volume
        fields = ('id', 'container_path', 'host_path', 'mode')


class PortMappingSerializer(serializers.ModelSerializer):
    class Meta:
        model = PortMapping
        fields = ('id', 'container_port', 'host_port', 'service_port')


class DeploymentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Deployment
        fields = ('id', 'marathon_id', 'deployed_service', 'deployed_environment', 'target_version', 'source_version',
                  'initiated_by', 'deployment_status', 'started_at', 'last_updated')


class EnvironmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Environment
        fields = ('id', 'name', 'geo_region', 'availability', 'marathon_master')


class GroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = Group
        fields = ('id', 'name')


class PermissionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Permission
        fields = ('id', 'service', 'user_group', 'can_deploy')


class BlockerSerializer(serializers.ModelSerializer):
    class Meta:
        model = Blocker
        fields = ('id', 'blocked_service', 'blocked_environment', 'blocked_user_group', 'description', 'created_by')


class NotificationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Notification
        fields = ('id', 'name', 'mailing_list','slack_channel', 'send_on')


class WatcherSerializer(serializers.ModelSerializer):
    class Meta:
        model = Watcher
        fields = ('id', 'user', 'object_type', 'object_id', 'notifications')


class DeployableVersionSerializer(serializers.ModelSerializer):
    class Meta:
        model = DeployableVersion
        fields = ('id', 'git_commit_sha', 'github_repository_url', 'related_service')