package io.logz.apollo.excpetions;

/**
 * Created by roiravhon on 1/30/17.
 */
public class ApolloParseException extends Exception {
    public ApolloParseException() {
    }

    public ApolloParseException(String message) {
        super(message);
    }

    public ApolloParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloParseException(Throwable cause) {
        super(cause);
    }

    public ApolloParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
