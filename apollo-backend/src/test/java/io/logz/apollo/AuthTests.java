package io.logz.apollo;

import io.logz.apollo.auth.User;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloCouldNotLoginException;
import io.logz.apollo.exceptions.ApolloCouldNotSignupException;
import io.logz.apollo.exceptions.ApolloNotAuthorizedException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.StandaloneApollo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by roiravhon on 11/22/16.
 */
public class AuthTests {

    private static final Logger logger = LoggerFactory.getLogger(AuthTests.class);
    private final StandaloneApollo standaloneApollo;

    public AuthTests() throws ScriptException, IOException, SQLException {

        standaloneApollo = StandaloneApollo.getOrCreateServer();
    }

    @Test
    public void testSignup() throws Exception {

        ApolloTestAdminClient apolloTestAdminClient = standaloneApollo.createTestAdminClient();
        ApolloTestClient apolloTestClient = standaloneApollo.createTestClient();

        // Check that user that is not authenticated cannot sign up
        assertThatThrownBy(() -> apolloTestAdminClient.signup(apolloTestClient.getClientUser(), Common.DEFAULT_PASSWORD)).isInstanceOf(ApolloNotAuthorizedException.class);

        // Login admin
        apolloTestAdminClient.login();

        // Signup the user
        apolloTestAdminClient.signup(apolloTestClient.getClientUser(), Common.DEFAULT_PASSWORD);

        // Make sure we cant signup again
        assertThatThrownBy(() -> apolloTestAdminClient.signup(apolloTestClient.getClientUser(), Common.DEFAULT_PASSWORD)).isInstanceOf(ApolloCouldNotSignupException.class);
    }

    @Test
    public void testLogin() throws Exception {

        ApolloTestAdminClient apolloTestAdminClient = standaloneApollo.createTestAdminClient();
        ApolloTestClient apolloTestClient = standaloneApollo.createTestClient();

        // Try to login before signup
        assertThatThrownBy(apolloTestClient::login).isInstanceOf(ApolloCouldNotLoginException.class);

        // Login admin and signup user
        apolloTestAdminClient.login();
        apolloTestAdminClient.signup(apolloTestClient.getClientUser(), Common.DEFAULT_PASSWORD);

        // Login the new user
        apolloTestClient.login();
    }

    @Test
    public void testGetAllUsers() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Get all users
        List<User> allUsers = apolloTestClient.getAllUsers();

        // Find our user in the list
        Optional<User> userFromApi = allUsers.stream().filter(user -> user.getUserEmail().equals(apolloTestClient.getClientUser().getUserEmail())).findFirst();

        boolean userFound = false;
        if (userFromApi.isPresent()) {

            if (userFromApi.get().getFirstName().equals(apolloTestClient.getClientUser().getFirstName()) &&
                userFromApi.get().getLastName().equals(apolloTestClient.getClientUser().getLastName()) &&
                userFromApi.get().isAdmin() == apolloTestClient.getClientUser().isAdmin() &&
                userFromApi.get().getHashedPassword().contains("*")) {

                userFound = true;
            }
        }

        assertThat(userFound).isTrue();
    }
}
