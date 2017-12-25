package io.logz.apollo.kubernetes;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.GroupDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Group;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import io.logz.apollo.models.Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 11/21/16.
 */
@Singleton
public class KubernetesMonitor {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesMonitor.class);
    private static final int TIMEOUT_TERMINATION = 60;
    private static final String UNKNOWN_GIT_COMMIT_SHA = "unknown";
    public static final String LOCAL_RUN_PROPERTY = "localrun";

    private final ScheduledExecutorService scheduledExecutorService;
    private final KubernetesHandlerStore kubernetesHandlerStore;
    private final ApolloConfiguration apolloConfiguration;
    private final EnvironmentDao environmentDao;
    private final DeploymentDao deploymentDao;
    private final ServiceDao serviceDao;
    private final GroupDao groupDao;

    @Inject
    public KubernetesMonitor(KubernetesHandlerStore kubernetesHandlerStore, ApolloConfiguration apolloConfiguration,
                             EnvironmentDao environmentDao, DeploymentDao deploymentDao, ServiceDao serviceDao, GroupDao groupDao) {
        this.kubernetesHandlerStore = requireNonNull(kubernetesHandlerStore);
        this.apolloConfiguration = requireNonNull(apolloConfiguration);
        this.environmentDao = requireNonNull(environmentDao);
        this.deploymentDao = requireNonNull(deploymentDao);
        this.serviceDao = requireNonNull(serviceDao);
        this.groupDao = requireNonNull(groupDao);

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("kubernetes-monitor-%d").build();
        scheduledExecutorService = Executors.newScheduledThreadPool(1, namedThreadFactory);
    }

    @PostConstruct
    public void start() {
        try {
            if (isLocalRun()) {
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
        if (isLocalRun()) return;

        try {
            logger.info("Stopping kubernetes monitoring thread");
            scheduledExecutorService.shutdown();
            scheduledExecutorService.awaitTermination(TIMEOUT_TERMINATION, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Got interrupt while waiting for ordinarily termination of the monitoring thread, force close.");
            scheduledExecutorService.shutdownNow();
        }
    }

    public void monitor() {
        // Defensive try, just to make sure nothing will close our executor service
        try {
            deploymentDao.getAllRunningDeployments().forEach(deployment -> {

                Environment relatedEnv = environmentDao.getEnvironment(deployment.getEnvironmentId());
                KubernetesHandler kubernetesHandler = kubernetesHandlerStore.getOrCreateKubernetesHandler(relatedEnv);

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

                if (deployment.getStatus().equals(Deployment.DeploymentStatus.DONE) || deployment.getStatus().equals(Deployment.DeploymentStatus.CANCELED)) {
                    updateDeploymentEnvStatus(deployment, getDeploymentCurrentEnvStatus(deployment, kubernetesHandler));
                }
            });
        } catch (Exception e) {
            logger.error("Got unexpected exception in the monitoring thread! swallow and moving on..", e);
        }
    }

    private boolean isLocalRun() {
        return Boolean.valueOf(System.getenv(LOCAL_RUN_PROPERTY)) || Boolean.valueOf(System.getProperty(LOCAL_RUN_PROPERTY));
    }

    private void updateDeploymentEnvStatus(Deployment deployment, Map envStatus) {
        try {
            JSONObject envStatusJson = new JSONObject(envStatus);
            deploymentDao.updateDeploymentEnvStatus(deployment.getId(), envStatusJson.toString());
        } catch (Exception e) {
            logger.error("Can't update environment status for deployment", e);
        }
    }

    private HashMap<Integer, Object> getDeploymentCurrentEnvStatus(Deployment deployment, KubernetesHandler kubernetesHandler) {

        HashMap<Integer, Object> envStatus = new HashMap<>();
        List<Integer> servicesDeployedOnEnv = deploymentDao.getServicesDeployedOnEnv(deployment.getEnvironmentId());

        servicesDeployedOnEnv.forEach(serviceId -> {
            Service service = serviceDao.getService(serviceId);
            if (service != null) {
                if (service.getIsPartOfGroup() != null && service.getIsPartOfGroup()) {
                    envStatus.put(serviceId, getServiceCommitShaWithGroup(kubernetesHandler, deployment, service));

                } else {
                    envStatus.put(serviceId, getServiceCommitSha(kubernetesHandler, deployment, service));
                }
            }
        });

        return envStatus;
    }

    private JSONObject getServiceCommitShaWithGroup(KubernetesHandler kubernetesHandler, Deployment deployment, Service service) {
        HashMap<Integer, String> serviceStatus = new HashMap<>();
        List<Group> relatedGroups = groupDao.getGroupsPerServiceAndEnvironment(service.getId(), deployment.getEnvironmentId());

        relatedGroups.forEach(group -> {
            if (group != null) {
                try {
                    KubernetesDeploymentStatus kubernetesDeploymentStatus = kubernetesHandler.getCurrentStatus(service, Optional.of(group.getName()));
                    if (kubernetesDeploymentStatus != null) {
                        serviceStatus.put(group.getId(), kubernetesDeploymentStatus.getGitCommitSha());
                    } else {
                        serviceStatus.put(group.getId(), UNKNOWN_GIT_COMMIT_SHA);
                        logger.warn("Can't get kubernetesDeploymentStatus for deployment {} and service {}", deployment.getId(), service.getId());
                    }
                } catch (Exception | Error e) {
                    serviceStatus.put(group.getId(), UNKNOWN_GIT_COMMIT_SHA);
                    logger.warn("Can't add service status to environment status for deployment " + deployment.getId() + " and service " + service.getId(), e);
                }
            }
        });

        return new JSONObject(serviceStatus);
    }

    private String getServiceCommitSha(KubernetesHandler kubernetesHandler, Deployment deployment, Service service) {
        try {
            KubernetesDeploymentStatus kubernetesDeploymentStatus = kubernetesHandler.getCurrentStatus(service, Optional.ofNullable(deployment.getGroupName()));
            if (kubernetesDeploymentStatus != null) {
                return kubernetesDeploymentStatus.getGitCommitSha();
            } else {
                logger.warn("Can't get kubernetesDeploymentStatus for deployment {} and service {}", deployment.getId(), service.getId());
            }
        } catch (Exception e) {
            logger.warn("Can't add service status to environment status for deployment " + deployment.getId() + " and service " + service.getId(), e);
        }

        return UNKNOWN_GIT_COMMIT_SHA;
    }
}
