package io.logz.apollo.notifications;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.slack.SlackSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

@Singleton
public class ApolloNotifications {

    private static final Logger logger = LoggerFactory.getLogger(ApolloNotifications.class);

    private static final int TIMEOUT_TERMINATION = 60;

    private static final int RETRIES = 3;
    private static final Duration SLEEP_BETWEEN_RETRIES = Duration.ofSeconds(1);

    private final ServiceDao serviceDao;
    private final EnvironmentDao environmentDao;

    private final BlockingQueue<Notification> queue;
    private final ExecutorService executor;
    private final SlackSender slackSender;

    public enum NotificationType {
        SLACK
    }

    @Inject
    public ApolloNotifications(ApolloConfiguration configuration, ServiceDao serviceDao, EnvironmentDao environmentDao) {
        this.serviceDao = requireNonNull(serviceDao);
        this.environmentDao = requireNonNull(environmentDao);
        queue = new LinkedBlockingQueue<>();
        slackSender = new SlackSender(configuration.getSlackWebHookUrl(), configuration.getSlackChanel());
        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::processQueue);
    }

    public void add(Deployment.DeploymentStatus status, Deployment deployment) {
        Environment environment = environmentDao.getEnvironment(deployment.getEnvironmentId());
        Service service = serviceDao.getService(deployment.getServiceId());
        Notification notification = new Notification(NotificationType.SLACK, deployment.getLastUpdate(),
                                                     status.toString(), service.getName(), environment.getName(),
                                                     deployment.getUserEmail(), deployment.getId());
        queue.add(notification);
    }

    @PreDestroy
    private void close() {
        try {
            logger.info("Stopping apollo notifications thread");
            executor.shutdown();
            executor.awaitTermination(TIMEOUT_TERMINATION, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Got interrupt while waiting for orderly termination of the notification thread, force close.");
            executor.shutdownNow();
        }

    }

    private void processQueue() {
        while(true) {
            try {
                Notification notification = queue.take();
                boolean success = false;
                for (int i = 0; i < RETRIES; i++) {
                    success = slackSender.send(notification);
                    if(success) break;
                    Thread.sleep(SLEEP_BETWEEN_RETRIES.toMillis());
                }
                if(!success) {
                    logger.warn("Failed to send a notification to {} after {} retries ", notification.type, RETRIES);
                }
            } catch (InterruptedException e) {
                logger.warn("Interrupted while waiting for an incoming notification", e);
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

}
