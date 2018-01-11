package io.logz.apollo.deployment;

import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.GroupDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Group;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import io.logz.apollo.models.Service;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class DeploymentEnvStatusManager {

    private static final String UNKNOWN_GIT_COMMIT_SHA = "unknown";

    private static final Logger logger = LoggerFactory.getLogger(DeploymentEnvStatusManager.class);

    private final DeploymentDao deploymentDao;
    private final ServiceDao serviceDao;
    private final GroupDao groupDao;

    @Inject
    public DeploymentEnvStatusManager(DeploymentDao deploymentDao, ServiceDao serviceDao, GroupDao groupDao) {
        this.deploymentDao = requireNonNull(deploymentDao);
        this.serviceDao = requireNonNull(serviceDao);
        this.groupDao = requireNonNull(groupDao);
    }

    public void updateDeploymentEnvStatus(Deployment deployment, Map envStatus) {
        try {
            JSONObject envStatusJson = new JSONObject(envStatus);
            deploymentDao.updateDeploymentEnvStatus(deployment.getId(), envStatusJson.toString());
        } catch (Exception e) {
            logger.error("Can't update environment status for deployment", e);
        }
    }

    public HashMap<Integer, Object> getDeploymentCurrentEnvStatus(Deployment deployment, KubernetesHandler kubernetesHandler) {

        HashMap<Integer, Object> envStatus = new HashMap<>();

        deploymentDao.getServicesDeployedOnEnv(deployment.getEnvironmentId()).forEach(serviceId -> {
            Service service = serviceDao.getService(serviceId);
            if (service != null) {
                if (service.getIsPartOfGroup() != null && service.getIsPartOfGroup()) {
                    envStatus.put(serviceId, getServiceCurrentStatusForServiceWithGroup(kubernetesHandler, deployment, service));
                } else {
                    String commitSha = getServiceCurrentStatusForServiceWithoutGroup(kubernetesHandler, deployment, service);
                    if (!commitSha.equals(UNKNOWN_GIT_COMMIT_SHA)) {
                        envStatus.put(serviceId, commitSha);
                    }
                }
            }
        });

        return envStatus;
    }

    private JSONObject getServiceCurrentStatusForServiceWithGroup(KubernetesHandler kubernetesHandler, Deployment deployment, Service service) {
        HashMap<Integer, String> serviceStatus = new HashMap<>();
        List<Group> relatedGroups = groupDao.getGroupsPerServiceAndEnvironment(service.getId(), deployment.getEnvironmentId());

        relatedGroups.forEach(group -> {
            if (group != null) {
                try {
                    KubernetesDeploymentStatus kubernetesDeploymentStatus = kubernetesHandler.getCurrentStatus(service, Optional.of(group.getName()));
                    if (kubernetesDeploymentStatus != null) {
                        serviceStatus.put(group.getId(), kubernetesDeploymentStatus.getGitCommitSha());
                    } else {
                        logger.warn("Can't get kubernetesDeploymentStatus for deployment {} and service {}", deployment.getId(), service.getId());
                    }
                } catch (Exception | Error e) {
                    logger.warn("Can't add service status to environment status for deployment " + deployment.getId() + " and service " + service.getId(), e);
                }
            }
        });

        return new JSONObject(serviceStatus);
    }

    private String getServiceCurrentStatusForServiceWithoutGroup(KubernetesHandler kubernetesHandler, Deployment deployment, Service service) {
        try {
            KubernetesDeploymentStatus kubernetesDeploymentStatus = kubernetesHandler.getCurrentStatus(service);
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
