package io.logz.apollo.controllers;

import com.google.common.collect.ImmutableMap;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.NotificationDao;
import io.logz.apollo.models.Notification.NotificationType;
import io.logz.apollo.models.Notification;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.PUT;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;

@Controller
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationDao notificationDao;

    @Inject
    public NotificationController(NotificationDao notificationDao) {
        this.notificationDao = requireNonNull(notificationDao);
    }

    @LoggedIn
    @GET("/notification")
    public List<Notification> getAllNotifications() {
        return notificationDao.getAllNotifications();
    }

    @LoggedIn
    @GET("/notification/{id}")
    public Notification getNotification(int id) {
        return notificationDao.getNotification(id);
    }

    @LoggedIn
    @POST("/notification")
    public void addNotification(String name, String environmentId, String serviceId, NotificationType type, String notificationJsonConfiguration, Req req) {

        Integer environmentIdParsed = null;
        Integer serviceIdParsed = null;

        if (environmentId != null && !environmentId.equals("null"))
            environmentIdParsed = Integer.parseInt(environmentId);

        if (serviceId != null && !serviceId.equals("null"))
            serviceIdParsed = Integer.parseInt(serviceId);

        Notification notification = new Notification();

        notification.setName(name);
        notification.setEnvironmentId(environmentIdParsed);
        notification.setServiceId(serviceIdParsed);
        notification.setType(type);
        notification.setNotificationJsonConfiguration(notificationJsonConfiguration);

        notificationDao.addNotification(notification);
        assignJsonResponseToReq(req, HttpStatus.CREATED, notification);
    }

    @LoggedIn
    @PUT("/notification/{id}")
    public void updateNotification(int id, String name, String environmentId, String serviceId, NotificationType type, String notificationJsonConfiguration, Req req) {

        Notification notification = notificationDao.getNotification(id);

        if (notification == null) {
            Map<String, String> message = ImmutableMap.of("message", "Notification not found");
            assignJsonResponseToReq(req, HttpStatus.NOT_FOUND, message);
            return;
        }

        Integer environmentIdParsed = null;
        Integer serviceIdParsed = null;

        if (environmentId != null && !environmentId.equals("null"))
            environmentIdParsed = Integer.parseInt(environmentId);

        if (serviceId != null && !serviceId.equals("null"))
            serviceIdParsed = Integer.parseInt(serviceId);

        notification.setName(name);
        notification.setEnvironmentId(environmentIdParsed);
        notification.setServiceId(serviceIdParsed);
        notification.setType(type);
        notification.setNotificationJsonConfiguration(notificationJsonConfiguration);

        notificationDao.updateNotification(notification);
        assignJsonResponseToReq(req, HttpStatus.OK, notification);
    }

    @LoggedIn
    @DELETE("/notification/{id}")
    public void deleteNotification(int id, Req req) {
        notificationDao.deleteNotification(id);
        assignJsonResponseToReq(req, HttpStatus.OK, "deleted");
    }
}
