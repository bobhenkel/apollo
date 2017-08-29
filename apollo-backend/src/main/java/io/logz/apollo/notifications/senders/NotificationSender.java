package io.logz.apollo.notifications.senders;

import io.logz.apollo.notifications.NotificationTemplateMetadata;

public interface NotificationSender {
    boolean send(NotificationTemplateMetadata notificationTemplateMetadata);
}
