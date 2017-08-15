package io.logz.apollo.notifications;

import java.util.Date;

public class Notification {

    public final ApolloNotifications.NotificationType type;
    public final Date lastUpdate;
    public final String status;
    public final String serviceName;
    public final String environmentName;
    public final String userEmail;
    public int deploymentId;

    public Notification(ApolloNotifications.NotificationType type, Date lastUpdate, String status, String serviceName, String environmentName, String userEmail, int id) {
        this.type = type;
        this.lastUpdate = lastUpdate;
        this.status = status;
        this.serviceName = serviceName;
        this.environmentName = environmentName;
        this.userEmail = userEmail;
        this.deploymentId = id;
    }
}
