package io.logz.apollo.clients;

import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.exceptions.ApolloCouldNotLoginException;
import io.logz.apollo.exceptions.ApolloCouldNotSignupException;
import io.logz.apollo.exceptions.ApolloNotAuthenticatedException;
import io.logz.apollo.exceptions.ApolloNotAuthorizedException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.RestResponse;

import java.io.IOException;

/**
 * Created by roiravhon on 11/24/16.
 */
public class ApolloAdminClient {

    private final GenericApolloClient genericApolloClient;

    public ApolloAdminClient(User adminUser, String plainAdminPassword, ApolloConfiguration apolloConfiguration) {
        genericApolloClient = new GenericApolloClient(adminUser.getUserEmail(), plainAdminPassword, apolloConfiguration);
    }

    public void login() throws IOException, ApolloCouldNotLoginException {
        genericApolloClient.login();
    }

    public void signup(User signupUser, String plainPassword) throws IOException, ApolloCouldNotSignupException, ApolloNotAuthorizedException, ApolloNotAuthenticatedException {
        RestResponse response = genericApolloClient.post("/signup", generateSignupJson(signupUser, plainPassword));
        switch (response.getCode()) {
            case 200:
                break;
            case 401:
                throw new ApolloNotAuthenticatedException();
            case 403:
                throw new ApolloNotAuthorizedException();
            default:
                throw new ApolloCouldNotSignupException();
        }
    }

    private String generateSignupJson(User user, String plainPassword) {
        return Common.generateJson( "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "userEmail", user.getUserEmail(),
                                    "password", plainPassword);
    }
}
