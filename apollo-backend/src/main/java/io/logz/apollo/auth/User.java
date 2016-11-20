package io.logz.apollo.auth;

/**
 * Created by roiravhon on 11/20/16.
 */
public class User {

    private final String emailAddress;
    private final String hashedPassword;

    public User(String emailAddress, String hashedPassword) {
        this.emailAddress = emailAddress;
        this.hashedPassword = hashedPassword;
    }
}
