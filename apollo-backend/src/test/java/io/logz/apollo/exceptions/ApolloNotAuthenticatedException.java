package io.logz.apollo.exceptions;

/**
 * Created by roiravhon on 12/18/16.
 */
public class ApolloNotAuthenticatedException extends ApolloClientException {

    public ApolloNotAuthenticatedException() {
    }

    public ApolloNotAuthenticatedException(String message) {
        super(message);
    }

    public ApolloNotAuthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloNotAuthenticatedException(Throwable cause) {
        super(cause);
    }

    public ApolloNotAuthenticatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
