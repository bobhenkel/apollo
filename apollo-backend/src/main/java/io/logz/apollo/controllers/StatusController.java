package io.logz.apollo.controllers;

import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import io.logz.apollo.models.Service;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 2/22/17.
 */
@Controller
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final EnvironmentDao environmentDao;
    private final ServiceDao serviceDao;

    @Inject
    public StatusController(EnvironmentDao environmentDao, ServiceDao serviceDao) {
        this.environmentDao = requireNonNull(environmentDao);
        this.serviceDao = requireNonNull(serviceDao);
    }

    @GET("/status/service/{id}")
    public List<KubernetesDeploymentStatus> getCurrentServiceStatus(int id) {
        List<KubernetesDeploymentStatus> kubernetesDeploymentStatusList = new ArrayList<>();
        Service service = serviceDao.getService(id);

        environmentDao.getAllEnvironments().forEach(environment -> {
            try {
                kubernetesDeploymentStatusList.add(KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment).getCurrentStatus(service));
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
        KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

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
    @GET("/status/logs/environment/{environmentId}/service/{serviceId}")
    public String getLogs(int environmentId, int serviceId) {
        Environment environment = environmentDao.getEnvironment(environmentId);
        Service service = serviceDao.getService(serviceId);
        KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

        return kubernetesHandler.getDeploymentLogs(environment, service);
    }
}
