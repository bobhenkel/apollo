package io.logz.apollo.common;

import okhttp3.Response;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;

import java.nio.ByteBuffer;

/**
 * Created by roiravhon on 3/21/17.
 */
public class ControllerCommon {
    public static void assignJsonResponseToReq(Req req, int code, Object json) {
        req.response().code(code);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(json);
    }

    public static void assignJsonBytesToReq(Req req, int code, byte[] bytes) {
        req.response().code(code);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().body(bytes);
    }
}
