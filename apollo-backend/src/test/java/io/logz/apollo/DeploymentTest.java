package io.logz.apollo;

import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by roiravhon on 1/5/17.
 */
public class DeploymentTest {

    @Test
    public void testGetAndAddDeployment() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment testDeployment = createAndSubmitDeployment(apolloTestClient);

        Deployment returnedDeployment = apolloTestClient.getDeployment(testDeployment.getId());

        assertThat(returnedDeployment.getEnvironmentId()).isEqualTo(testDeployment.getEnvironmentId());
        assertThat(returnedDeployment.getServiceId()).isEqualTo(testDeployment.getServiceId());
        assertThat(returnedDeployment.getDeployableVersionId()).isEqualTo(testDeployment.getDeployableVersionId());
        assertThat(returnedDeployment.getUserEmail()).isEqualTo(apolloTestClient.getClientUser().getUserEmail());
        assertThat(returnedDeployment.getStatus()).isEqualTo(Deployment.DeploymentStatus.PENDING);
    }

    @Test
    public void testGetAllDeployments() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment testDeployment = createAndSubmitDeployment(apolloTestClient);

        Optional<Deployment> deploymentFromApi = apolloTestClient.getAllDeployments().stream()
                .filter(deployment -> deployment.getId() == testDeployment.getId()).findFirst();

        boolean found = false;

        if (deploymentFromApi.isPresent()) {
            if (deploymentFromApi.get().getEnvironmentId() == testDeployment.getEnvironmentId() &&
                    deploymentFromApi.get().getServiceId() == testDeployment.getServiceId() &&
                    deploymentFromApi.get().getDeployableVersionId() == testDeployment.getDeployableVersionId() &&
                    deploymentFromApi.get().getStatus().toString().equals(Deployment.DeploymentStatus.PENDING.toString())) {
                found = true;
            }
        }

        assertThat(found).isTrue();
    }

    @Test
    public void testSimultaneousDeployments() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment deployment1 = createAndSubmitDeployment(apolloTestClient);

        // Submit that again to verify we can't run the same one twice
        assertThatThrownBy(() -> apolloTestClient.addDeployment(deployment1)).isInstanceOf(ApolloClientException.class);

        // Just to make sure we are not blocking different deployments to run on the same time
        createAndSubmitDeployment(apolloTestClient);
    }

    @Test
    public void testGitCommitShaCorrectlyPlaced() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment firstDeployment = createAndSubmitDeployment(apolloTestClient);
        DeployableVersion createdDeployableVersionFromFirstDeployment = apolloTestClient.getDeployableVersion(firstDeployment.getDeployableVersionId());
        Service createdServiceFromFirstDeployment = apolloTestClient.getService(firstDeployment.getServiceId());
        Environment createdEnvironmentFromFirstDeployment = apolloTestClient.getEnvironment(firstDeployment.getEnvironmentId());

        DeployableVersion secondDeployableVersion = ModelsGenerator.createDeployableVersion(createdServiceFromFirstDeployment);
        secondDeployableVersion.setId(apolloTestClient.addDeployableVersion(secondDeployableVersion).getId());

        Common.markDeploymentAsCompletedViaDB(firstDeployment);

        Deployment secondDeployment = ModelsGenerator.createDeployment(createdServiceFromFirstDeployment, createdEnvironmentFromFirstDeployment, secondDeployableVersion);
        Deployment returnedSecondDeployment = apolloTestClient.addDeployment(secondDeployment);

        assertThat(returnedSecondDeployment.getSourceVersion()).isEqualTo(createdDeployableVersionFromFirstDeployment.getGitCommitSha());
    }

    private Deployment createAndSubmitDeployment(ApolloTestClient apolloTestClient) throws Exception {

        // Add all foreign keys
        Environment testEnvironment = ModelsGenerator.createEnvironment();
        testEnvironment.setId(apolloTestClient.addEnvironment(testEnvironment).getId());

        Service testService = ModelsGenerator.createService();
        testService.setId(apolloTestClient.addService(testService).getId());

        DeployableVersion testDeployableVersion = ModelsGenerator.createDeployableVersion(testService);
        testDeployableVersion.setId(apolloTestClient.addDeployableVersion(testDeployableVersion).getId());

        // Give the user permissions to deploy
        grantUserFullPermissionsOnEnvironment(apolloTestClient, testEnvironment);

        // Now we have enough to create a deployment
        Deployment testDeployment = ModelsGenerator.createDeployment(testService, testEnvironment, testDeployableVersion);
        testDeployment.setId(apolloTestClient.addDeployment(testDeployment).getId());

        return testDeployment;
    }

    private void grantUserFullPermissionsOnEnvironment(ApolloTestClient apolloTestClient, Environment environment) throws Exception {

        ApolloTestAdminClient apolloTestAdminClient = Common.getAndLoginApolloTestAdminClient();

        DeploymentGroup newDeploymentGroup = ModelsGenerator.createDeploymentGroup();
        newDeploymentGroup.setId(apolloTestAdminClient.addDeploymentGroup(newDeploymentGroup).getId());

        DeploymentPermission newDeploymentPermission = ModelsGenerator.createAllowDeploymentPermission(Optional.of(environment), Optional.empty());
        newDeploymentPermission.setId(apolloTestAdminClient.addDeploymentPermission(newDeploymentPermission).getId());

        apolloTestAdminClient.addDeploymentPermissionToDeploymentGroup(newDeploymentGroup.getId(), newDeploymentPermission.getId());
        apolloTestAdminClient.addUserToGroup(apolloTestClient.getClientUser().getUserEmail(), newDeploymentGroup.getId());
    }
}
