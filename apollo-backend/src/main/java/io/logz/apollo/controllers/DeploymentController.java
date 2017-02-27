package io.logz.apollo.controllers;

import io.logz.apollo.LockService;
import io.logz.apollo.auth.PermissionsValidator;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.models.KubernetesDeploymentStatus;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;
import java.util.Optional;

/**
 * Created by roiravhon on 1/5/17.
 */
@Controller
public class DeploymentController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentController.class);

    @LoggedIn
    @GET("/deployment")
    public List<Deployment> getAllDeployments() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            return deploymentDao.getAllDeployments();

        }
    }

    @LoggedIn
    @GET("/deployment/{id}")
    public Deployment getDeployment(int id) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            return deploymentDao.getDeployment(id);
        }
    }

    @LoggedIn
    @GET("/deployment/{id}/logs")
    public String getDeploymentLogs(int id) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);

            // TODO: ideally i would not need a KubernetesHandler here, but since no DI and desired simplicity - i can live with this for now
            Deployment deployment = deploymentDao.getDeployment(id);
            Environment environment = environmentDao.getEnvironment(deployment.getEnvironmentId());
            KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

            return kubernetesHandler.getDeploymentLogs(deployment);
        }
    }

    @LoggedIn
    @GET("/latest-deployments")
    public List<Deployment> getLatestDeployments() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            return deploymentDao.getLatestDeployments();
        }
    }

    @LoggedIn
    @GET("/running-deployments")
    public List<Deployment> getRunningDeployments() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            return deploymentDao.getAllRunningDeployments();
        }
    }

    @LoggedIn
    @GET("/running-and-just-finished-deployments")
    public List<Deployment> getRunningAndJustFinishedDeployments() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            return deploymentDao.getRunningAndJustFinishedDeployments();
        }
    }

    @LoggedIn
    @POST("/deployment")
    public void addDeployment(int environmentId, int serviceId, int deployableVersionId, Req req) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            DeploymentPermissionDao deploymentPermissionDao = apolloMyBatisSession.getDao(DeploymentPermissionDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);

            // Get the username from the token
            String userEmail = req.token().get("_user").toString();

            String sourceVersion = null;

            try {
                // Get the current commit sha from kubernetes so we can revert if necessary
                Environment environment = environmentDao.getEnvironment(environmentId);
                Service service = serviceDao.getService(serviceId);
                KubernetesDeploymentStatus kubernetesDeploymentStatus = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment).getCurrentStatus(service);

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

            if (!PermissionsValidator.isAllowedToDeploy(serviceId, environmentId,
                    deploymentPermissionDao.getPermissionsByUser(userEmail))) {

                logger.info("User is not authorized to perform this deployment!");
                assignJsonResponseToReq(req, 403, "Not Authorized!");

            } else {

                String lockName = LockService.getDeploymentLockName(serviceId, environmentId);
                if (!LockService.getAndObtainLock(lockName)) {
                    logger.warn("A deployment request of this sort is currently being run");
                    assignJsonResponseToReq(req, 429, "A deployment request is currently running for this service and environment! Wait until its done");
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

                        assignJsonResponseToReq(req, 409, "There is an on-going deployment for this service in this environment");
                        ;
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
                    assignJsonResponseToReq(req, 201, newDeployment);
                } finally {
                    LockService.releaseLock(lockName);
                }
            }
        }
    }

    @LoggedIn
    @DELETE("/deployment/{id}")
    public void cancelDeployment(int id, Req req) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);

            // Get the username from the token
            String userEmail = req.token().get("_user").toString();

            MDC.put("userEmail", userEmail);
            MDC.put("deploymentId", String.valueOf(id));

            logger.info("Got request for a deployment cancellation");

            String lockName = LockService.getDeploymentCancelationName(id);
            if (!LockService.getAndObtainLock(lockName)) {
                logger.warn("A deployment cancel request of this sort is currently being run");
                assignJsonResponseToReq(req, 429, "A deployment cancel request is currently running for this deployment! Wait until its done");
                return;
            }
            try {
                Deployment deployment = deploymentDao.getDeployment(id);

                // Check that the deployment is not already done, or canceled
                if (!deployment.getStatus().equals(Deployment.DeploymentStatus.DONE) && !deployment.getStatus().equals(Deployment.DeploymentStatus.CANCELED)) {
                    logger.info("Setting deployment to status PENDING_CANCELLATION");
                    deploymentDao.updateDeploymentStatus(id, Deployment.DeploymentStatus.PENDING_CANCELLATION);
                    assignJsonResponseToReq(req, 202, "Deployment Canceled!");
                } else {
                    logger.warn("Deployment is in status {}, can't cancel it now!", deployment.getStatus());
                    assignJsonResponseToReq(req, 400, "Can't cancel the deployment as it is not in a state that's allows canceling");
                }
            } finally {
                LockService.releaseLock(lockName);
            }
        }
    }
}
