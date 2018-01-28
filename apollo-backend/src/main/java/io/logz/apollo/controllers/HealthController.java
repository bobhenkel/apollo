package io.logz.apollo.controllers;

import com.google.inject.Inject;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.kubernetes.KubernetesHealth;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.http.Req;

import java.util.Map;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 3/2/17.
 */
@Controller
public class HealthController {

    private final KubernetesHealth kubernetesHealth;

    @Inject
    public HealthController(KubernetesHealth kubernetesHealth) {
        this.kubernetesHealth= requireNonNull(kubernetesHealth);
    }

    @GET("/health")
    public void getHealth(Req req) {
        Map<Integer, Boolean> environmentsHealthMap = kubernetesHealth.getEnvironmentsHealthMap();
        if (environmentsHealthMap.containsValue(false)) {
            assignJsonResponseToReq(req, HttpStatus.INTERNAL_SERVER_ERROR, environmentsHealthMap);
        } else {
            assignJsonResponseToReq(req, HttpStatus.OK, environmentsHealthMap);
        }
    }
}
