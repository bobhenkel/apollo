package io.logz.apollo.helpers;

/**
 * Created by roiravhon on 11/28/16.
 */
public class RestResponse {

    private final int code;
    private final String body;

    public RestResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
