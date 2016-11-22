package io.logz.apollo.auth;

/**
 * Created by roiravhon on 11/20/16.
 */
public class User {

    private final String emailAddress;
    private final String firstName;
    private final String lastName;
    private final String hashedPassword;
    private final Boolean isAdmin;

    public User(String emailAddress, String firstName, String lastName, String hashedPassword, Boolean isAdmin) {
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hashedPassword = hashedPassword;
        this.isAdmin = isAdmin;
    }

    // Another C'tor to represent **** as passwords when returning to the user
    public User(String emailAddress, String firstName, String lastName, Boolean isAdmin) {
        this(emailAddress, firstName, lastName, "*********", isAdmin);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
