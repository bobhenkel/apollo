package io.logz.apollo.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import io.logz.apollo.auth.User;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.exceptions.ApolloCouldNotLoginException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.RestResponse;
import io.logz.apollo.models.Environment;

import java.io.IOException;
import java.util.Arrays;
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

    public List<User> getAllUsers() throws ApolloClientException {
        try {
            RestResponse restResponse = genericApolloClient.get("/user");

            if (restResponse.getCode() != 200) {
                throw new ApolloClientException("Could not get users! got response code=" + restResponse.getCode() + " from server, with response=" + restResponse.getBody());
            }

            ObjectMapper mapper = new ObjectMapper();
            return Arrays.asList(mapper.readValue(restResponse.getBody(), User[].class));

        } catch (IOException e) {
            throw new ApolloClientException("Could not reach Apollo API!", e);
        }
    }

    public void addEnvironment(Environment environment) throws ApolloClientException {
        try {
            RestResponse restResponse = genericApolloClient.post("/environment",
                    Common.generateJson("name", environment.getName(), "geoRegion", environment.getGeoRegion(),
                            "availability", environment.getAvailability(), "kubernetesMaster", environment.getKubernetesMaster(),
                            "kubernetesToken", environment.getKubernetesToken()));

            if (restResponse.getCode() != 201) {
                throw new ApolloClientException("Got HTTP return code " + restResponse.getCode() + " with text: " + restResponse.getBody());
            }
        } catch (IOException e) {
            throw new ApolloClientException("Could not reach Apollo API!", e);
        }
    }

    public Environment getEnvironment(String name) throws ApolloClientException {
        try {
            RestResponse restResponse = genericApolloClient.get("/environment/" + name);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(restResponse.getBody(), Environment.class);

        } catch (IOException e) {
            throw new ApolloClientException("Could not reach Apollo API!", e);
        }
    }

    public List<Environment> getAllEnvironments() throws ApolloClientException {
        try {
            RestResponse restResponse = genericApolloClient.get("/environment");
            ObjectMapper mapper = new ObjectMapper();
            return Arrays.asList(mapper.readValue(restResponse.getBody(), Environment[].class));

        } catch (IOException e) {
            throw new ApolloClientException("Could not reach Apollo API!", e);
        }
    }
}
