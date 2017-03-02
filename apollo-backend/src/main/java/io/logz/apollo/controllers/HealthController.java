package io.logz.apollo.controllers;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.http.Req;

/**
 * Created by roiravhon on 3/2/17.
 */
@Controller
public class HealthController extends BaseController {

    @GET("/health")
    public void getHealth(Req req) {
        //TODO: Implement some sort of health check
        assignJsonResponseToReq(req, 200, "ok");
    }
}
