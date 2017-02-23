package io.logz.apollo.controllers;

import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.models.Status;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import java.util.LinkedList;
import java.util.List;

import static io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;

/**
 * Created by roiravhon on 2/22/17.
 */
@Controller
public class StatusController {

    @GET("/status/service/{id}")
    public List<Status> getCurrentServiceStatus(int id) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            List<Status> statusList = new LinkedList<>();
            Service service = serviceDao.getService(id);

            environmentDao.getAllEnvironments().forEach(environment ->
                    statusList.add(KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment).getCurrentStatus(service)));

            return statusList;
        }
    }

    @GET("/status/environment/{id}")
    public List<Status> getCurrentEnvironmentStatus(int id) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            List<Status> statusList = new LinkedList<>();
            Environment environment = environmentDao.getEnvironment(id);
            KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

            serviceDao.getAllServices().forEach(service ->
                    statusList.add(kubernetesHandler.getCurrentStatus(service)));

            return statusList;
        }
    }
}
