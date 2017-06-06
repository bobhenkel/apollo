package io.logz.apollo;

import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.models.Environment;
import org.junit.Test;

import java.util.Optional;

import static io.logz.apollo.helpers.ModelsGenerator.createAndSubmitEnvironment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by roiravhon on 12/19/16.
 */
public class EnvironmentTest {

    @Test
    public void testAddAndGetEnvironment() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add environment
        Environment testEnvironment = createAndSubmitEnvironment(apolloTestClient);

        // Make sure we cant add that again
        assertThatThrownBy(() -> apolloTestClient.addEnvironment(testEnvironment)).isInstanceOf(ApolloClientException.class);

        // Get the environment back from the api and validate the returned value
        Environment returnedEnv = apolloTestClient.getEnvironment(testEnvironment.getId());

        assertThat(returnedEnv.getName()).isEqualTo(testEnvironment.getName());
        assertThat(returnedEnv.getGeoRegion()).isEqualTo(testEnvironment.getGeoRegion());
        assertThat(returnedEnv.getAvailability()).isEqualTo(testEnvironment.getAvailability());
        assertThat(returnedEnv.getKubernetesMaster()).isEqualTo(testEnvironment.getKubernetesMaster());
        assertThat(returnedEnv.getKubernetesNamespace()).isEqualTo(testEnvironment.getKubernetesNamespace());
        assertThat(returnedEnv.getKubernetesToken()).contains("*");
    }

    @Test
    public void testGetAllEnvironments() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add environment
        Environment testEnvironment = createAndSubmitEnvironment(apolloTestClient);

        // Get all environments, and filter for ours
        Optional<Environment> environmentFromApi = apolloTestClient.getAllEnvironments().stream()
                .filter(environment -> environment.getId() == testEnvironment.getId()).findFirst();

        boolean found = false;

        // Make sure we got the correct one
        if (environmentFromApi.isPresent()) {
            if (environmentFromApi.get().getName().equals(testEnvironment.getName()) &&
                    environmentFromApi.get().getGeoRegion().equals(testEnvironment.getGeoRegion()) &&
                    environmentFromApi.get().getAvailability().equals(testEnvironment.getAvailability()) &&
                    environmentFromApi.get().getKubernetesMaster().equals(testEnvironment.getKubernetesMaster()) &&
                    environmentFromApi.get().getKubernetesToken().contains("*") &&
                    environmentFromApi.get().getKubernetesNamespace().equals(testEnvironment.getKubernetesNamespace())) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }
}
