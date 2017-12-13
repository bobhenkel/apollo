package io.logz.apollo.excpetions;

public class ApolloDeploymentBlockedException extends ApolloDeploymentException {
    public ApolloDeploymentBlockedException(String message) {
        super(message);
    }

    public ApolloDeploymentBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloDeploymentBlockedException(Throwable cause) {
        super(cause);
    }

    public ApolloDeploymentBlockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ApolloDeploymentBlockedException() {
    }
}
