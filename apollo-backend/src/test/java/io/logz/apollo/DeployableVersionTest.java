package io.logz.apollo;

import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Service;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 12/20/16.
 */
public class DeployableVersionTest {

    @Test
    public void testAddAndGetDeployableVersion() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add deployable version
        DeployableVersion testDeployableVersion = createAndSubmitDeployableVersion(apolloTestClient);

        // Get it from REST
        DeployableVersion returnedDeployableVersion = apolloTestClient.getDeployableVersion(testDeployableVersion.getId());

        assertThat(returnedDeployableVersion.getGitCommitSha()).isEqualTo(testDeployableVersion.getGitCommitSha());
        assertThat(returnedDeployableVersion.getGithubRepositoryUrl()).isEqualTo(testDeployableVersion.getGithubRepositoryUrl());
        assertThat(returnedDeployableVersion.getServiceId()).isEqualTo(testDeployableVersion.getServiceId());
    }

    @Test
    public void testGetAllDeployableVersions() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add deployable version
        DeployableVersion testDeployableVersion = createAndSubmitDeployableVersion(apolloTestClient);

        // Get all deployable versions, and filter for ours
        Optional<DeployableVersion> deployableVersionFromApi = apolloTestClient.getAllDeployableVersions().stream()
                .filter(deployableVersion -> deployableVersion.getId() == testDeployableVersion.getId()).findFirst();

        boolean found = false;

        // Make sure we got the correct one
        if (deployableVersionFromApi.isPresent()) {
            if (deployableVersionFromApi.get().getGitCommitSha().equals(testDeployableVersion.getGitCommitSha()) &&
                    deployableVersionFromApi.get().getGithubRepositoryUrl().equals(testDeployableVersion.getGithubRepositoryUrl()) &&
                    deployableVersionFromApi.get().getServiceId() == testDeployableVersion.getServiceId()) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }

    private DeployableVersion createAndSubmitDeployableVersion(ApolloTestClient apolloTestClient) throws ApolloClientException {

        // Needed for FK
        Service testService = ModelsGenerator.createService();
        testService.setId(apolloTestClient.addService(testService).getId());

        // Add deployable version
        DeployableVersion testDeployableVersion = ModelsGenerator.createDeployableVersion(testService);
        testDeployableVersion.setId(apolloTestClient.addDeployableVersion(testDeployableVersion).getId());

        return testDeployableVersion;
    }
}
