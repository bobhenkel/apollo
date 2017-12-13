package io.logz.apollo.controllers;

import io.logz.apollo.deployment.DeploymentHandler;
import io.logz.apollo.LockService;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.excpetions.ApolloDeploymentBlockedException;
import io.logz.apollo.excpetions.ApolloDeploymentConflictException;
import io.logz.apollo.excpetions.ApolloDeploymentTooManyRequestsException;
import io.logz.apollo.models.Deployment;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.util.List;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 1/5/17.
 */
@Controller
public class DeploymentController {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentController.class);

    private final DeploymentDao deploymentDao;
    private final LockService lockService;
    private final DeploymentHandler deploymentHandler;

    @Inject
    public DeploymentController(DeploymentDao deploymentDao, LockService lockService, DeploymentHandler deploymentHandler) {
        this.deploymentDao = requireNonNull(deploymentDao);
        this.lockService = requireNonNull(lockService);
        this.deploymentHandler = requireNonNull(deploymentHandler);
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
    @GET("/latest-deployments")
    public List<Deployment> getLatestDeployments() {
        return deploymentDao.getLatestDeployments();
    }

    @LoggedIn
    @GET("/running-deployments")
    public List<Deployment> getRunningDeployments() {
        return deploymentDao.getAllRunningDeployments();
    }

    @LoggedIn
    @GET("/running-and-just-finished-deployments")
    public List<Deployment> getRunningAndJustFinishedDeployments() {
        return deploymentDao.getRunningAndJustFinishedDeployments();
    }

    @LoggedIn
    @GET("/deployment/{id}/envstatus")
    public String getDeploymentEnvStatus(int id) {
        return deploymentDao.getDeploymentEnvStatus(id);
    }

    @LoggedIn
    @POST("/deployment")
    public void addDeployment(int environmentId, int serviceId, int deployableVersionId, Req req) {
        try {
            Deployment deployment = deploymentHandler.addDeployment(environmentId, serviceId, deployableVersionId, req);
            assignJsonResponseToReq(req, HttpStatus.CREATED, deployment);
        } catch (ApolloDeploymentBlockedException e) {
            assignJsonResponseToReq(req, HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ApolloDeploymentConflictException e) {
            assignJsonResponseToReq(req, HttpStatus.CONFLICT, e.getMessage());
        } catch (ApolloDeploymentTooManyRequestsException e) {
            assignJsonResponseToReq(req, HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        } catch (Exception e) {
            assignJsonResponseToReq(req, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @LoggedIn
    @DELETE("/deployment/{id}")
    public void cancelDeployment(int id, Req req) {
        String lockName = lockService.getDeploymentCancelationName(id);
        try {
            if (!lockService.getAndObtainLock(lockName)) {
                logger.warn("A deployment cancel request is currently running for this deployment! Wait until its done");
                assignJsonResponseToReq(req, HttpStatus.TOO_MANY_REQUESTS, "A deployment cancel request is currently running for this deployment! Wait until its done");
                return;
            }

            // Get the username from the token
            String userEmail = req.token().get("_user").toString();

            MDC.put("userEmail", userEmail);
            MDC.put("deploymentId", String.valueOf(id));

            logger.info("Got request for a deployment cancellation");

            Deployment deployment = deploymentDao.getDeployment(id);

            // Check that the deployment is not already done, or canceled
            if (!deployment.getStatus().equals(Deployment.DeploymentStatus.DONE) && !deployment.getStatus().equals(Deployment.DeploymentStatus.CANCELED)) {
                logger.info("Setting deployment to status PENDING_CANCELLATION");
                deploymentDao.updateDeploymentStatus(id, Deployment.DeploymentStatus.PENDING_CANCELLATION);
                assignJsonResponseToReq(req, HttpStatus.ACCEPTED, "Deployment Canceled!");
            } else {
                logger.warn("Deployment is in status {}, can't cancel it now!", deployment.getStatus());
                assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Can't cancel the deployment as it is not in a state that's allows canceling");
            }
        } finally {
            lockService.releaseLock(lockName);
        }
    }
}
