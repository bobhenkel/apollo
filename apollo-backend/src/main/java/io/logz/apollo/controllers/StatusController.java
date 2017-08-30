package io.logz.apollo.controllers;

import io.logz.apollo.common.ControllerCommon;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerStore;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import io.logz.apollo.models.Service;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 2/22/17.
 */
@Controller
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final KubernetesHandlerStore kubernetesHandlerStore;
    private final EnvironmentDao environmentDao;
    private final ServiceDao serviceDao;

    @Inject
    public StatusController(KubernetesHandlerStore kubernetesHandlerStore, EnvironmentDao environmentDao,
                            ServiceDao serviceDao) {
        this.kubernetesHandlerStore = requireNonNull(kubernetesHandlerStore);
        this.environmentDao = requireNonNull(environmentDao);
        this.serviceDao = requireNonNull(serviceDao);
    }

    @GET("/status/service/{id}")
    public List<KubernetesDeploymentStatus> getCurrentServiceStatus(int id) {
        List<KubernetesDeploymentStatus> kubernetesDeploymentStatusList = new ArrayList<>();
        Service service = serviceDao.getService(id);

        environmentDao.getAllEnvironments().forEach(environment -> {
            try {
                kubernetesDeploymentStatusList.add(kubernetesHandlerStore.getOrCreateKubernetesHandler(environment).getCurrentStatus(service));
            } catch (Exception e) {
                logger.warn("Could not get status of service {} on environment {}! trying others..", id, environment.getId(), e);
            }
        });

        return kubernetesDeploymentStatusList;
    }

    @GET("/status/environment/{id}")
    public List<KubernetesDeploymentStatus> getCurrentEnvironmentStatus(int id) {
        List<KubernetesDeploymentStatus> kubernetesDeploymentStatusList = new ArrayList<>();
        Environment environment = environmentDao.getEnvironment(id);
        KubernetesHandler kubernetesHandler = kubernetesHandlerStore.getOrCreateKubernetesHandler(environment);

        serviceDao.getAllServices().forEach(service -> {
            try {
                kubernetesDeploymentStatusList.add(kubernetesHandler.getCurrentStatus(service));
            } catch (Exception e) {
                logger.warn("Could not get status of service {} on environment {}! trying others..", service.getId(), id, e);
            }
        });

        return kubernetesDeploymentStatusList;
    }

    @LoggedIn
    @GET("/status/environment/{environmentId}/service/{serviceId}/latestpod")
    public String getLatestPodName(int environmentId, int serviceId, Req req) {
        Environment environment = environmentDao.getEnvironment(environmentId);
        Service service = serviceDao.getService(serviceId);
        KubernetesHandler kubernetesHandler = kubernetesHandlerStore.getOrCreateKubernetesHandler(environment);

        Optional<String> serviceLatestCreatedPodName = kubernetesHandler.getServiceLatestCreatedPodName(service);
        if (serviceLatestCreatedPodName.isPresent()) {
            return serviceLatestCreatedPodName.get();
        } else {
            ControllerCommon.assignJsonResponseToReq(req, HttpStatus.NOT_FOUND, "Can't find pod");
            return "";
        }
    }

    @LoggedIn
    @GET("/status/environment/{environmentId}/pod/{podName}/containers")
    public List<String> getPodContainers(int environmentId, String podName) {
        Environment environment = environmentDao.getEnvironment(environmentId);
        KubernetesHandler kubernetesHandler = kubernetesHandlerStore.getOrCreateKubernetesHandler(environment);

        return kubernetesHandler.getPodContainerNames(podName);
    }
}
