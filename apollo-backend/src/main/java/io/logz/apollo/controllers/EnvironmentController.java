package io.logz.apollo.controllers;

import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import io.logz.apollo.models.Environment;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import java.util.List;
import java.util.stream.Collectors;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;

/**
 * Created by roiravhon on 12/19/16.
 */
@Controller
public class EnvironmentController {

    @LoggedIn
    @GET("/environment")
    public List<Environment> getEnvironments() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            return environmentDao.getAllEnvironments().stream().map(environment -> {
                environment = maskCredentials(environment);
                return environment;
            }).collect(Collectors.toList());
        }
    }

    @LoggedIn
    @GET("/environment/{id}")
    public Environment getEnvironment(int id) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            Environment gotEnvironment = environmentDao.getEnvironment(id);
            gotEnvironment = maskCredentials(gotEnvironment);
            return gotEnvironment;
        }
    }

    @LoggedIn
    @POST("/environment")
    public void addEnvironment(String name, String geoRegion, String availability, String kubernetesMaster,
                               String kubernetesToken, String kubernetesNamespace, Req req) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            Environment newEnvironment = new Environment();
            newEnvironment.setName(name);
            newEnvironment.setGeoRegion(geoRegion);
            newEnvironment.setAvailability(availability);
            newEnvironment.setKubernetesMaster(kubernetesMaster);
            newEnvironment.setKubernetesToken(kubernetesToken);
            newEnvironment.setKubernetesNamespace(kubernetesNamespace);

            environmentDao.addEnvironment(newEnvironment);
            assignJsonResponseToReq(req, HttpStatus.CREATED, newEnvironment);
        }
    }

    private Environment maskCredentials(Environment environment) {
        environment.setKubernetesToken("******");
        return environment;
    }
}
