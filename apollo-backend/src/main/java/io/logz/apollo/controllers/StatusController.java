package io.logz.apollo.controllers;

import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;

/**
 * Created by roiravhon on 2/22/17.
 */
@Controller
public class StatusController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @GET("/status/service/{id}")
    public List<KubernetesDeploymentStatus> getCurrentServiceStatus(int id) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            List<KubernetesDeploymentStatus> kubernetesDeploymentStatusList = new LinkedList<>();
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
    }

    @GET("/status/environment/{id}")
    public List<KubernetesDeploymentStatus> getCurrentEnvironmentStatus(int id) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            List<KubernetesDeploymentStatus> kubernetesDeploymentStatusList = new LinkedList<>();
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
    }

    @LoggedIn
    @GET("/status/logs/environment/{environmentId}/service/{serviceId}")
    public String getLogs(int environmentId, int serviceId) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);

            Environment environment = environmentDao.getEnvironment(environmentId);
            Service service = serviceDao.getService(serviceId);
            KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

            return kubernetesHandler.getDeploymentLogs(environment, service);
        }
    }
}
