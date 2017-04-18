package io.logz.apollo.controllers;

import io.logz.apollo.common.ControllerCommon;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Environment;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import static io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;

/**
 * Created by roiravhon on 4/13/17.
 */
@Controller
public class KubernetesActionsController {

    @LoggedIn
    @POST("/k8s/pod/restart")
    public void restartPod(int environmentId, String podName, Req req) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            Environment environment = environmentDao.getEnvironment(environmentId);

            if (environment == null) {
                ControllerCommon.assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Environment " + environmentId + " does not exists");
                return;
            }

            KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment).restartPod(podName);
            ControllerCommon.assignJsonResponseToReq(req, HttpStatus.OK, "Ok");
        }
    }
}
