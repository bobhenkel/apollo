package io.logz.apollo.excpetions;

public class ApolloDeploymentException extends Exception {
    public ApolloDeploymentException() {
    }

    public ApolloDeploymentException(String message) {
        super(message);
    }

    public ApolloDeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloDeploymentException(Throwable cause) {
        super(cause);
    }

    public ApolloDeploymentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
