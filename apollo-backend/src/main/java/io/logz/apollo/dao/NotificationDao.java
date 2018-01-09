package io.logz.apollo.dao;

import io.logz.apollo.models.Notification;

import java.util.List;

public interface NotificationDao {
    Notification getNotification(int id);
    List<Notification> getAllNotifications();
    void addNotification(Notification notification);
    void updateNotification(Notification notification);
    void deleteNotification(int id);
}
