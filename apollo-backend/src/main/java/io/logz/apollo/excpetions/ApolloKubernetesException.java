package io.logz.apollo.excpetions;

public class ApolloKubernetesException extends Exception {
    public ApolloKubernetesException() {

    }

    public ApolloKubernetesException(String message) {
        super(message);
    }

    public ApolloKubernetesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloKubernetesException(Throwable cause) {
        super(cause);
    }

    public ApolloKubernetesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
