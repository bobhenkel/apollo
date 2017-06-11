package io.logz.apollo.exceptions;

/**
 * Created by roiravhon on 6/5/17.
 */
public class ApolloBlockedException extends ApolloClientException {

    public ApolloBlockedException() {
    }

    public ApolloBlockedException(String message) {
        super(message);
    }

    public ApolloBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloBlockedException(Throwable cause) {
        super(cause);
    }

    public ApolloBlockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
