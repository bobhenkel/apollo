package io.logz.apollo;

import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.models.Environment;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by roiravhon on 12/19/16.
 */
public class EnvironmentTests {

    private final StandaloneApollo standaloneApollo;

    public EnvironmentTests() throws ScriptException, IOException, SQLException {
        standaloneApollo = StandaloneApollo.getOrCreateServer();
    }

    @Test
    public void testAddAndGetEnvironment() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add environment
        Environment testEnvironment = Common.createEnvironment();
        apolloTestClient.addEnvironment(testEnvironment);

        // Make sure we cant add that again
        assertThatThrownBy(() -> apolloTestClient.addEnvironment(testEnvironment)).isInstanceOf(ApolloClientException.class);

        // Get the environment back from the api and validate the returned value
        Environment returnedEnv = apolloTestClient.getEnvironment(testEnvironment.getName());

        assertThat(returnedEnv.getName()).isEqualTo(testEnvironment.getName());
        assertThat(returnedEnv.getGeoRegion()).isEqualTo(testEnvironment.getGeoRegion());
        assertThat(returnedEnv.getAvailability()).isEqualTo(testEnvironment.getAvailability());
        assertThat(returnedEnv.getKubernetesMaster()).isEqualTo(testEnvironment.getKubernetesMaster());
        assertThat(returnedEnv.getKubernetesToken()).contains("*");
    }

    @Test
    public void testGetAllEnvironments() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add environment
        Environment testEnvironment = Common.createEnvironment();
        apolloTestClient.addEnvironment(testEnvironment);

        // Get all environments, and filter for ours
        Optional<Environment> environmentFromApi = apolloTestClient.getAllEnvironments().stream()
                .filter(environment -> environment.getName().equals(testEnvironment.getName())).findFirst();

        boolean found = false;

        // Make sure we got the correct one
        if (environmentFromApi.isPresent()) {
            if (environmentFromApi.get().getName().equals(testEnvironment.getName()) &&
                    environmentFromApi.get().getGeoRegion().equals(testEnvironment.getGeoRegion()) &&
                    environmentFromApi.get().getAvailability().equals(testEnvironment.getAvailability()) &&
                    environmentFromApi.get().getKubernetesMaster().equals(testEnvironment.getKubernetesMaster()) &&
                    environmentFromApi.get().getKubernetesToken().contains("*")) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }
}
