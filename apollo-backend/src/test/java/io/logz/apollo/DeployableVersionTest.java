package io.logz.apollo;

import io.logz.apollo.clients.ApolloClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Service;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.rapidoid.serialize.Ser;

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
    public void populateRealCommitDetails() throws ApolloClientException {

        //TODO: when apollo goes open source, change the commit here to one of apollos commits

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Service createdService = createAndSubmitService(apolloTestClient);

        DeployableVersion deployableVersion = new DeployableVersion();
        deployableVersion.setGithubRepositoryUrl("https://github.com/kubernetes/kubernetes");
        deployableVersion.setGitCommitSha("b3d627c2e2a801e442b7a75ee8cddc33c7663812"); // Just a random commit in k8s repo
        deployableVersion.setServiceId(createdService.getId());

        deployableVersion.setId(apolloTestClient.addDeployableVersion(deployableVersion).getId());

        DeployableVersion returnedDeployableVersion = apolloTestClient.getDeployableVersion(deployableVersion.getId());

        assertThat(returnedDeployableVersion.getCommitMessage()).contains("Improved code coverage");
        assertThat(returnedDeployableVersion.getCommitUrl()).isEqualTo("https://github.com/kubernetes/kubernetes/commit/b3d627c2e2a801e442b7a75ee8cddc33c7663812");
        assertThat(returnedDeployableVersion.getCommitterName()).isEqualTo("GitHub Web Flow");
        assertThat(returnedDeployableVersion.getCommitterAvatarUrl()).contains("avatars");
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

    @Test
    public void testGetDeployableVersionFromSha() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        DeployableVersion testDeployableVersion = createAndSubmitDeployableVersion(apolloTestClient);
        DeployableVersion returnedDeployableVersion = apolloTestClient.getDeployableVersionFromSha(testDeployableVersion.getGitCommitSha());

        assertThat(testDeployableVersion.getId()).isEqualTo(returnedDeployableVersion.getId());
    }

    private DeployableVersion createAndSubmitDeployableVersion(ApolloTestClient apolloTestClient) throws ApolloClientException {

        // Needed for FK
        Service testService = createAndSubmitService(apolloTestClient);

        // Add deployable version
        DeployableVersion testDeployableVersion = ModelsGenerator.createDeployableVersion(testService);
        testDeployableVersion.setId(apolloTestClient.addDeployableVersion(testDeployableVersion).getId());

        return testDeployableVersion;
    }

    @NotNull
    private Service createAndSubmitService(ApolloTestClient apolloTestClient) throws ApolloClientException {
        Service testService = ModelsGenerator.createService();
        testService.setId(apolloTestClient.addService(testService).getId());
        return testService;
    }
}
