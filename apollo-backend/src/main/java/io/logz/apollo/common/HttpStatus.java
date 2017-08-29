package io.logz.apollo.common;

public class HttpStatus {

    // Implementing this class since there are missing codes in all of built in java classes, and have an external dependency just for this seems too much.

    public static int OK = 200;
    public static int CREATED = 201;
    public static int ACCEPTED = 202;

    public static int BAD_REQUEST = 400;
    public static int UNAUTHORIZED = 401;
    public static int FORBIDDEN = 403;
    public static int NOT_FOUND = 404;
    public static int NOT_ACCEPTABLE = 406;
    public static int CONFLICT = 409;
    public static int TOO_MANY_REQUESTS = 429;
    
    public static int INTERNAL_SERVER_ERROR = 500;
  
    public static boolean isPositive(int code) {
        return code >= 200 && code < 300;
    }
}
