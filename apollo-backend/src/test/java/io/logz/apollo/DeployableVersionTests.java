package io.logz.apollo;

import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Service;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 12/20/16.
 */
public class DeployableVersionTests {

    private final StandaloneApollo standaloneApollo;

    public DeployableVersionTests() throws ScriptException, IOException, SQLException {
        standaloneApollo = StandaloneApollo.getOrCreateServer();
    }

    @Test
    public void testAddAndGetDeployableVersion() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add service (needed foreign key)
        Service testService = ModelsGenerator.createService();
        Service createdService = apolloTestClient.addService(testService);

        // Add deployable version
        DeployableVersion testDeployableVersion = ModelsGenerator.createDeployableVersion(createdService);
        DeployableVersion createdDeployableVersion = apolloTestClient.addDeployableVersion(testDeployableVersion);

        DeployableVersion returnedDeployableVersion = apolloTestClient.getDeployableVersion(createdDeployableVersion.getId());

        assertThat(returnedDeployableVersion.getGitCommitSha()).isEqualTo(testDeployableVersion.getGitCommitSha());
        assertThat(returnedDeployableVersion.getGithubRepositoryUrl()).isEqualTo(testDeployableVersion.getGithubRepositoryUrl());
        assertThat(returnedDeployableVersion.getRelatedService()).isEqualTo(createdService.getId());
    }

    @Test
    public void testGetAllDeployableVersions() throws ApolloClientException {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        // Add service (needed foreign key)
        Service testService = ModelsGenerator.createService();
        Service createdService = apolloTestClient.addService(testService);

        // Add deployable version
        DeployableVersion testDeployableVersion = ModelsGenerator.createDeployableVersion(createdService);
        DeployableVersion createdDeployableVersion = apolloTestClient.addDeployableVersion(testDeployableVersion);

        // Get all deployable versions, and filter for ours
        Optional<DeployableVersion> deployableVersionFromApi = apolloTestClient.getAllDeployableVersions().stream()
                .filter(deployableVersion -> deployableVersion.getId() == createdDeployableVersion.getId()).findFirst();

        boolean found = false;

        // Make sure we got the correct one
        if (deployableVersionFromApi.isPresent()) {
            if (deployableVersionFromApi.get().getGitCommitSha().equals(testDeployableVersion.getGitCommitSha()) &&
                    deployableVersionFromApi.get().getGithubRepositoryUrl().equals(testDeployableVersion.getGithubRepositoryUrl()) &&
                    deployableVersionFromApi.get().getRelatedService() == createdService.getId()) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }
}
