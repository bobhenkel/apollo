package io.logz.apollo.exceptions;

/**
 * Created by roiravhon on 11/28/16.
 */
public class ApolloClientException extends Exception {

    public ApolloClientException() {
    }

    public ApolloClientException(String message) {
        super(message);
    }

    public ApolloClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloClientException(Throwable cause) {
        super(cause);
    }

    public ApolloClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
