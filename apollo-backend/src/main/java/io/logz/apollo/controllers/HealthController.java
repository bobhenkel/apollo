package io.logz.apollo.controllers;

import io.logz.apollo.common.HttpStatus;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.http.Req;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;

/**
 * Created by roiravhon on 3/2/17.
 */
@Controller
public class HealthController {

    @GET("/health")
    public void getHealth(Req req) {
        //TODO: Implement some sort of health check
        assignJsonResponseToReq(req, HttpStatus.OK, "ok");
    }
}
