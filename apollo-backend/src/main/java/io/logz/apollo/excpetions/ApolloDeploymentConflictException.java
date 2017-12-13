package io.logz.apollo.excpetions;

public class ApolloDeploymentConflictException extends ApolloDeploymentException {
    public ApolloDeploymentConflictException(String message) {
        super(message);
    }

    public ApolloDeploymentConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloDeploymentConflictException(Throwable cause) {
        super(cause);
    }

    public ApolloDeploymentConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ApolloDeploymentConflictException() {
    }
}
