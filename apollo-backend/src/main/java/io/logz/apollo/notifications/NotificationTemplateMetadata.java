package io.logz.apollo.notifications;

import java.util.Date;

public class NotificationTemplateMetadata {

    private final Date lastUpdate;
    private final String status;
    private final String serviceName;
    private final String groupName;
    private final String environmentName;
    private final String userEmail;
    private int deploymentId;
    private ApolloNotifications.NotificationType type;
    private String notificationJsonConfiguration;

    NotificationTemplateMetadata(Date lastUpdate, String status, String serviceName,
                                 String environmentName, String userEmail, int deploymentId, String groupName,
                                 ApolloNotifications.NotificationType type, String notificationJsonConfiguration) {
        this.lastUpdate = lastUpdate;
        this.status = status;
        this.serviceName = serviceName;
        this.environmentName = environmentName;
        this.userEmail = userEmail;
        this.deploymentId = deploymentId;
        this.groupName = groupName;
        this.type = type;
        this.notificationJsonConfiguration = notificationJsonConfiguration;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getStatus() {
        return status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getDeploymentId() {
        return deploymentId;
    }

    public String getGroupName() { return groupName; }

    public ApolloNotifications.NotificationType getType() {
        return type;
    }

    public String getNotificationJsonConfiguration() {
        return notificationJsonConfiguration;
    }
}
