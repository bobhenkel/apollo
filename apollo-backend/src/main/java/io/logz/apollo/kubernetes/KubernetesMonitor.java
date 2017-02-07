package io.logz.apollo.kubernetes;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by roiravhon on 11/21/16.
 */
public class KubernetesMonitor {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesMonitor.class);
    private static final int TIMEOUT_TERMINATION = 60;
    private final ApolloConfiguration apolloConfiguration;
    private final EnvironmentDao environmentDao;
    private final DeploymentDao deploymentDao;
    private final ScheduledExecutorService scheduledExecutorService;

    public KubernetesMonitor(ApolloConfiguration apolloConfiguration) {

        this.apolloConfiguration = apolloConfiguration;

        environmentDao = ApolloMyBatis.getDao(EnvironmentDao.class);
        deploymentDao = ApolloMyBatis.getDao(DeploymentDao.class);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 0, apolloConfiguration.getMonitorThreadFrequencySeconds(), TimeUnit.SECONDS);
    }

    public void stop() {
        try {
            scheduledExecutorService.shutdown();
            scheduledExecutorService.awaitTermination(TIMEOUT_TERMINATION, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            logger.error("Got interrupt while waiting for ordinarily termination of the monitoring thread, force close.");
            scheduledExecutorService.shutdownNow();
        }
    }

    private void monitor() {

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
