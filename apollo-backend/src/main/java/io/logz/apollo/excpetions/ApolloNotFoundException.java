package io.logz.apollo.excpetions;

public class ApolloNotFoundException extends Exception {
    public ApolloNotFoundException() {
    }

    public ApolloNotFoundException(String message) {
        super(message);
    }

    public ApolloNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloNotFoundException(Throwable cause) {
        super(cause);
    }

    public ApolloNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
