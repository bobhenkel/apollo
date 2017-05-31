package io.logz.apollo.controllers;

import io.logz.apollo.LockService;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.PermissionsValidator;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import io.logz.apollo.models.Service;
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
import java.util.Optional;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 1/5/17.
 */
@Controller
public class DeploymentController {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentController.class);

    private final KubernetesHandlerFactory kubernetesHandlerFactory;
    private final DeploymentPermissionDao deploymentPermissionDao;
    private final EnvironmentDao environmentDao;
    private final DeploymentDao deploymentDao;
    private final ServiceDao serviceDao;
    private final LockService lockService;

    @Inject
    public DeploymentController(KubernetesHandlerFactory kubernetesHandlerFactory,
                                DeploymentPermissionDao deploymentPermissionDao, EnvironmentDao environmentDao,
                                DeploymentDao deploymentDao, ServiceDao serviceDao, LockService lockService) {
        this.kubernetesHandlerFactory = requireNonNull(kubernetesHandlerFactory);
        this.deploymentPermissionDao = requireNonNull(deploymentPermissionDao);
        this.environmentDao = requireNonNull(environmentDao);
        this.deploymentDao = requireNonNull(deploymentDao);
        this.serviceDao = requireNonNull(serviceDao);
        this.lockService = requireNonNull(lockService);
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
    @GET("/deployment/{id}/logs")
    public String getDeploymentLogs(int id) {
        // TODO: ideally i would not need a KubernetesHandler here, but since no DI and desired simplicity - i can live with this for now
        Deployment deployment = deploymentDao.getDeployment(id);
        Environment environment = environmentDao.getEnvironment(deployment.getEnvironmentId());
        Service service = serviceDao.getService(deployment.getServiceId());
        KubernetesHandler kubernetesHandler = kubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

        return kubernetesHandler.getDeploymentLogs(environment, service);
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
    @POST("/deployment")
    public void addDeployment(int environmentId, int serviceId, int deployableVersionId, Req req) {
        // Get the username from the token
        String userEmail = req.token().get("_user").toString();
        String sourceVersion = null;

        try {
            // Get the current commit sha from kubernetes so we can revert if necessary
            Environment environment = environmentDao.getEnvironment(environmentId);
            Service service = serviceDao.getService(serviceId);
            KubernetesDeploymentStatus kubernetesDeploymentStatus = kubernetesHandlerFactory.getOrCreateKubernetesHandler(environment).getCurrentStatus(service);

            if (kubernetesDeploymentStatus != null)
                sourceVersion = kubernetesDeploymentStatus.getGitCommitSha();

        } catch (Exception e) {
            logger.error("Got exception while getting the current gitCommitSha from kubernetes. That means no revert.", e);
        }

        MDC.put("environmentId", String.valueOf(environmentId));
        MDC.put("serviceId", String.valueOf(serviceId));
        MDC.put("deployableVersionId", String.valueOf(deployableVersionId));
        MDC.put("userEmail", userEmail);
        MDC.put("sourceVersion", sourceVersion);

        logger.info("Got request for a new deployment");

        List<DeploymentPermission> userPermissions = deploymentPermissionDao.getPermissionsByUser(userEmail);
        if (!PermissionsValidator.isAllowedToDeploy(serviceId, environmentId, userPermissions)) {
            logger.info("User is not authorized to perform this deployment!");
            assignJsonResponseToReq(req, HttpStatus.FORBIDDEN, "Not Authorized!");
            return;
        }

        String lockName = lockService.getDeploymentLockName(serviceId, environmentId);
        if (!lockService.getAndObtainLock(lockName)) {
            logger.warn("A deployment request of this sort is currently being run");
            assignJsonResponseToReq(req, HttpStatus.TOO_MANY_REQUESTS, "A deployment request is currently running for this service and environment! Wait until its done");
            return;
        }

        try {
            logger.info("Permissions verified, Checking that no other deployment is currently running");

            Optional<Deployment> runningDeployment = deploymentDao.getAllRunningDeployments()
                    .stream()
                    .filter(deployment ->
                            deployment.getServiceId() == serviceId &&
                                    deployment.getEnvironmentId() == environmentId)
                    .findAny();

            if (runningDeployment.isPresent()) {
                logger.warn("There is already a running deployment that initiated by {}. Can't start a new one",
                        runningDeployment.get().getUserEmail());

                assignJsonResponseToReq(req, HttpStatus.CONFLICT, "There is an on-going deployment for this service in this environment");
                return;
            }

            logger.info("All checks passed. Running deployment");

            Deployment newDeployment = new Deployment();
            newDeployment.setEnvironmentId(environmentId);
            newDeployment.setServiceId(serviceId);
            newDeployment.setDeployableVersionId(deployableVersionId);
            newDeployment.setUserEmail(userEmail);
            newDeployment.setStatus(Deployment.DeploymentStatus.PENDING);
            newDeployment.setSourceVersion(sourceVersion);

            deploymentDao.addDeployment(newDeployment);
            assignJsonResponseToReq(req, HttpStatus.CREATED, newDeployment);
        } finally {
            lockService.releaseLock(lockName);
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
