package io.logz.apollo.clients;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.exceptions.ApolloCouldNotLoginException;
import io.logz.apollo.helpers.RestResponse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by roiravhon on 11/24/16.
 */
public class ApolloClient {

    private final GenericApolloClient genericApolloClient;
    private final User user;

    public ApolloClient(User user, String plainPassword, ApolloConfiguration apolloConfiguration) {
        this.user = user;
        genericApolloClient = new GenericApolloClient(user.getUserEmail(), plainPassword, apolloConfiguration);
    }

    public void login() throws IOException, ApolloCouldNotLoginException {
        genericApolloClient.login();
    }

    public User getClientUser() {
        return user;
    }

    public List<User> getAllUsers() throws IOException, ApolloClientException {
        RestResponse restResponse = genericApolloClient.get("/users");

        if (restResponse.getCode() != 200) {
            throw new ApolloClientException("Could not get users! got response code=" + restResponse.getCode() + " from server, with response=" + restResponse.getBody());
        }

        List<User> userList = new LinkedList<>();
        JsonArray jsonArray = new JsonParser().parse(restResponse.getBody()).getAsJsonArray();

        jsonArray.forEach(user -> {
            JsonObject currUser = user.getAsJsonObject();
            User tempUser = new User();
            tempUser.setUserEmail(currUser.get("userEmail").getAsString());
            tempUser.setFirstName(currUser.get("firstName").getAsString());
            tempUser.setLastName(currUser.get("lastName").getAsString());
            tempUser.setHashedPassword(currUser.get("hashedPassword").getAsString());
            tempUser.setAdmin(currUser.get("admin").getAsBoolean());
            userList.add(tempUser);
        });

        return userList;
    }
}
