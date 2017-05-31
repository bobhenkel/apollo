package io.logz.apollo.kubernetes;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by roiravhon on 11/21/16.
 */
public class KubernetesMonitor {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesMonitor.class);
    private static final int TIMEOUT_TERMINATION = 60;
    private final ApolloConfiguration apolloConfiguration;
    private final ScheduledExecutorService scheduledExecutorService;
    private final boolean localrun;

    @Inject
    public KubernetesMonitor(ApolloConfiguration apolloConfiguration) {
        this.apolloConfiguration = apolloConfiguration;
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("kubernetes-monitor-%d").build();
        scheduledExecutorService = Executors.newScheduledThreadPool(1, namedThreadFactory);
        this.localrun = Boolean.valueOf(System.getenv("localrun"));
    }

    @PostConstruct
    public void start() {
        try {
            if (localrun) {
                logger.info("Running in local-mode, kubernetes monitor thread is not up.");
                return;
            }

            logger.info("Starting kubernetes monitor thread");
            int monitorThreadFrequency = apolloConfiguration.getMonitorThreadFrequencySeconds();
            scheduledExecutorService.scheduleAtFixedRate(this::monitor, 0, monitorThreadFrequency, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Could not start kubernetes monitor thread! Bailing..", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (localrun) return;

        try {
            logger.info("Stopping kubernetes monitoring thread");
            scheduledExecutorService.shutdown();
            scheduledExecutorService.awaitTermination(TIMEOUT_TERMINATION, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Got interrupt while waiting for ordinarily termination of the monitoring thread, force close.");
            scheduledExecutorService.shutdownNow();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void monitor() {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            // Defensive try, just to make sure nothing will close our executor service
            try {
                deploymentDao.getAllRunningDeployments().forEach(deployment -> {

                    Environment relatedEnv = environmentDao.getEnvironment(deployment.getEnvironmentId());
                    KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(relatedEnv);

                    Deployment returnedDeployment;

                    switch (deployment.getStatus()) {
                        case PENDING:
                            returnedDeployment = kubernetesHandler.startDeployment(deployment);
                            break;
                        case PENDING_CANCELLATION:
                            returnedDeployment = kubernetesHandler.cancelDeployment(deployment);
                            break;
                        default:
                            returnedDeployment = kubernetesHandler.monitorDeployment(deployment);
                            break;
                    }

                    deploymentDao.updateDeploymentStatus(deployment.getId(), returnedDeployment.getStatus());

                });
            } catch (Exception e) {
                logger.error("Got unexpected exception in the monitoring thread! swallow and moving on..", e);
            }
        }
    }
}
