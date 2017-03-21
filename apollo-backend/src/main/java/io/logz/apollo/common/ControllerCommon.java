package io.logz.apollo.common;

import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;

/**
 * Created by roiravhon on 3/21/17.
 */
public class ControllerCommon {
    public static void assignJsonResponseToReq(Req req, int code, Object json) {
        req.response().code(code);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(json);
    }
}
