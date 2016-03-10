from django.contrib.auth.models import User
from django.db import models


class Service(models.Model):
    NETWORK_TYPE = (
        "BRIDGE",
        "HOST"
    )

    name = models.CharField(max_length=100)
    notifications = models.ManyToManyField(Notification, null=True)

    # Marathon related
    number_of_instances = models.PositiveIntegerField()
    marathon_id = models.CharField(max_length=100)
    cpus = models.FloatField()
    memory = models.PositiveIntegerField()
    image_name = models.CharField(max_length=500)

    # Marathon related optional
    command = models.CharField(max_length=100, null=True)
    port_mappings = models.ManyToManyField(PortMappings, null=True)
    volumes = models.ManyToManyField(Volumes, null=True)
    environment_variable = models.ManyToManyField(EnvironmentVariable, null=True)
    constraints = models.ManyToManyField(Constraints, null=True)
    labels = models.ManyToManyField(Labels, null=True)
    health_checks = models.ManyToManyField(HealthChecks, null=True)

    # Marathon related with defaults
    network_type = models.CharField(max_length=10, choices=NETWORK_TYPE, default=NETWORK_TYPE[0])
    privileged = models.BooleanField(default=False)
    upgrade_minimum_health_capacity = models.FloatField(default=1)
    upgrade_maximum_over_capacity = models.FloatField(default=1)


class HealthChecks(models.Model):
    PROTOCOLS = (
        "HTTP",
        "TCP",
        "COMMAND"
    )

    protocol = models.CharField(max_length=10, choices=PROTOCOLS, default=PROTOCOLS[0])
    path = models.CharField(max_length=200, null=True)
    grace_period_seconds = models.PositiveIntegerField(null=True)
    interval_seconds = models.PositiveIntegerField(null=True)
    port_index = models.PositiveIntegerField(null=True)
    port = models.PositiveIntegerField(null=True)
    timeout_seconds = models.PositiveIntegerField(null=True)
    max_consecutive_failures = models.PositiveIntegerField(default=3)
    command = models.CharField(max_length=3000, null=True)


class Labels(models.Model):
    name = models.CharField(max_length=300)
    value = models.CharField(max_length=300)


class Constraints(models.Model):
    attribute = models.CharField(max_length=200)
    operator = models.CharField(max_length=200)
    value = models.CharField(max_length=200)


class EnvironmentVariable(models.Model):
    name = models.CharField(max_length=300)
    value = models.CharField(max_length=300)


class Volumes(models.Model):
    MODES = (
        "RO",
        "RW"
    )

    container_path = models.CharField(max_length=100)
    host_path = models.CharField(max_length=100)
    mode = models.CharField(max_length=2, choices=MODES)


class PortMappings(models.Model):
    container_port = models.PositiveIntegerField(null=True)
    host_port = models.PositiveIntegerField(null=True)
    service_port = models.PositiveIntegerField(null=True)


class Deployment(models.Model):
    marathon_id = models.CharField(max_length=100)
    deployed_service = models.ForeignKey(Service)
    target_version = models.CharField(max_length=100)
    source_version = models.CharField(max_length=100)
    initiate_by = models.ForeignKey(User)


class Environment(models.Model):
    GEO_REGIONS = (
        'us-east-1',
        'eu-west-1'
    )

    AVAILABILITIES = (
        'PROD',
        'STAGING',
        'DEV'
    )

    name = models.CharField(max_length=100)
    geo_region = models.CharField(max_length=20, choices=GEO_REGIONS)
    availability = models.CharField(max_length=10, choices=AVAILABILITIES)


class UserGroup(models.Model):
    name = models.CharField(max_length=100)


class Permissions(models.Model):
    service = models.ForeignKey(Service)
    user_group = models.ForeignKey(UserGroup)
    can_deploy = models.BooleanField()


class Blocker(models.Model):
    blocked_service = models.ForeignKey(Service, null=True)
    blocked_environment = models.ForeignKey(Environment, null=True)
    blocked_user_group = models.ForeignKey(UserGroup, null=True)
    description = models.CharField(max_length=1000)
    created_by = models.ForeignKey(User)


class Notification(models.Model):
    SEND_ON = (
        "success",
        "failure",
        "any"
    )

    name = models.CharField(max_length=100)
    mailing_list = models.EmailField(null=True)
    slack_channel = models.CharField(max_length=50, null=True)
    send_on = models.CharField(max_length=10, choices=SEND_ON)


class Watcher(models.Model):
    user = models.ForeignKey(User)
    object_type = models.CharField(max_length=2)
    object_id = models.PositiveIntegerField()
    notifications = models.ManyToManyField(Notification)