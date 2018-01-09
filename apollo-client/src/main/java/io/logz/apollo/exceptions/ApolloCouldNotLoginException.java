package io.logz.apollo.exceptions;

/**
 * Created by roiravhon on 11/24/16.
 */
public class ApolloCouldNotLoginException extends ApolloClientException {
    public ApolloCouldNotLoginException() {
    }

    public ApolloCouldNotLoginException(String message) {
        super(message);
    }

    public ApolloCouldNotLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloCouldNotLoginException(Throwable cause) {
        super(cause);
    }

    public ApolloCouldNotLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
