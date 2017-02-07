package io.logz.apollo.controllers;

import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;

/**
 * Created by roiravhon on 2/7/17.
 */
public abstract class BaseController {

    protected void assignJsonResponseToReq(Req req, int code, Object json) {
        req.response().code(code);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(json);
    }
}
