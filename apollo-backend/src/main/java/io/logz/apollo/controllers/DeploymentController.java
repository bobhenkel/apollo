package io.logz.apollo.controllers;

import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.models.Deployment;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import java.util.List;

/**
 * Created by roiravhon on 1/5/17.
 */
@Controller
public class DeploymentController {

    private final DeploymentDao deploymentDao;

    public DeploymentController() {
        this.deploymentDao = ApolloMyBatis.getDao(DeploymentDao.class);
    }

    @LoggedIn
    @GET("/deployment")
    public List<Deployment> getAllDeployments() {
        return deploymentDao.getAllDeployments();
    }

    @LoggedIn
    @GET("/deployment/{id}")
    public Deployment getDeployment(int id) {
        return deploymentDao.getDeployment(id);
    }

    @LoggedIn
    @POST("/deployment")
    public void addDeployment(int environmentId, int serviceId, int deployableVersionId,
                              String userEmail, String sourceVersion, Req req) {

        Deployment newDeployment = new Deployment();
        newDeployment.setEnvironmentId(environmentId);
        newDeployment.setServiceId(serviceId);
        newDeployment.setDeployableVersionId(deployableVersionId);
        newDeployment.setUserEmail(userEmail);
        newDeployment.setStatus(Deployment.DeploymentStatus.PENDING);
        newDeployment.setSourceVersion(sourceVersion);

        deploymentDao.addDeployment(newDeployment);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newDeployment);
    }
}
