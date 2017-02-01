package io.logz.apollo.excpetions;

/**
 * Created by roiravhon on 1/31/17.
 */
public class ApolloTransformException extends Exception {
    public ApolloTransformException() {
    }

    public ApolloTransformException(String message) {
        super(message);
    }

    public ApolloTransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloTransformException(Throwable cause) {
        super(cause);
    }

    public ApolloTransformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
