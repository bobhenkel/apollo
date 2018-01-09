package io.logz.apollo.exceptions;

/**
 * Created by roiravhon on 11/24/16.
 */
public class ApolloCouldNotSignupException extends ApolloClientException {
    public ApolloCouldNotSignupException() {
    }

    public ApolloCouldNotSignupException(String message) {
        super(message);
    }

    public ApolloCouldNotSignupException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApolloCouldNotSignupException(Throwable cause) {
        super(cause);
    }

    public ApolloCouldNotSignupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
