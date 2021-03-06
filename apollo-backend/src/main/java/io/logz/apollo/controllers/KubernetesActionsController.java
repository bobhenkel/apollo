package io.logz.apollo.controllers;

import io.logz.apollo.common.ControllerCommon;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.excpetions.ApolloKubernetesException;
import io.logz.apollo.kubernetes.KubernetesHandlerStore;
import io.logz.apollo.models.Environment;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 4/13/17.
 */
@Controller
public class KubernetesActionsController {

    private final KubernetesHandlerStore kubernetesHandlerStore;
    private final EnvironmentDao environmentDao;

    @Inject
    public KubernetesActionsController(KubernetesHandlerStore kubernetesHandlerStore, EnvironmentDao environmentDao) {
        this.kubernetesHandlerStore = requireNonNull(kubernetesHandlerStore);
        this.environmentDao = requireNonNull(environmentDao);
    }

    @LoggedIn
    @POST("/k8s/pod/restart")
    public void restartPod(int environmentId, String podName, Req req) {
        Environment environment = environmentDao.getEnvironment(environmentId);

        if (environment == null) {
            ControllerCommon.assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Environment " + environmentId + " does not exists");
            return;
        }

        try {
            kubernetesHandlerStore.getOrCreateKubernetesHandler(environment).restartPod(podName);
        } catch (ApolloKubernetesException e) {
            ControllerCommon.assignJsonResponseToReq(req, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        ControllerCommon.assignJsonResponseToReq(req, HttpStatus.OK, "Ok");
    }
}
