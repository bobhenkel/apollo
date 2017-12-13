package io.logz.apollo.excpetions;

public class ApolloDeploymentTooManyRequestsException extends ApolloDeploymentException {
    public ApolloDeploymentTooManyRequestsException(String message) {
        super(message);
    }

    public ApolloDeploymentTooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloDeploymentTooManyRequestsException(Throwable cause) {
        super(cause);
    }

    public ApolloDeploymentTooManyRequestsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ApolloDeploymentTooManyRequestsException() {
    }
}
