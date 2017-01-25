package io.logz.apollo;

import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.DeploymentGroupPermission;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.DeploymentUserGroup;
import io.logz.apollo.clients.ApolloTestAdminClient;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by roiravhon on 1/5/17.
 */
public class DeploymentTests {

    @Test
    public void testGetAndAddDeployment() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment testDeployment = createAndSumbitDeployment(apolloTestClient);

        Deployment returnedDeployment = apolloTestClient.getDeployment(testDeployment.getId());

        assertThat(returnedDeployment.getEnvironmentId()).isEqualTo(testDeployment.getEnvironmentId());
        assertThat(returnedDeployment.getServiceId()).isEqualTo(testDeployment.getServiceId());
        assertThat(returnedDeployment.getDeployableVersionId()).isEqualTo(testDeployment.getDeployableVersionId());
        assertThat(returnedDeployment.getUserEmail()).isEqualTo(testDeployment.getUserEmail());
        assertThat(returnedDeployment.getStatus()).isEqualTo(Deployment.DeploymentStatus.PENDING);
        assertThat(returnedDeployment.getSourceVersion()).isEqualTo(testDeployment.getSourceVersion());
    }

    @Test
    public void testGetAllDeployments() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();

        Deployment testDeployment = createAndSumbitDeployment(apolloTestClient);

        Optional<Deployment> deploymentFromApi = apolloTestClient.getAllDeployments().stream()
                .filter(deployment -> deployment.getId() == testDeployment.getId()).findFirst();

        boolean found = false;

        if (deploymentFromApi.isPresent()) {
            if (deploymentFromApi.get().getEnvironmentId() == testDeployment.getEnvironmentId() &&
                    deploymentFromApi.get().getServiceId() == testDeployment.getServiceId() &&
                    deploymentFromApi.get().getDeployableVersionId() == testDeployment.getDeployableVersionId() &&
                    deploymentFromApi.get().getStatus().toString().equals(Deployment.DeploymentStatus.PENDING.toString()) &&
                    deploymentFromApi.get().getSourceVersion().equals(testDeployment.getSourceVersion())) {
                found = true;
            }
        }

        assertThat(found).isTrue();
    }

    private Deployment createAndSumbitDeployment(ApolloTestClient apolloTestClient) throws Exception {

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
        Deployment testDeployment = ModelsGenerator.createDeployment(testService, testEnvironment, testDeployableVersion, apolloTestClient.getClientUser());
        testDeployment.setId(apolloTestClient.addDeployment(testDeployment).getId());

        return testDeployment;
    }

    private void grantUserFullPermissionsOnEnvironment(ApolloTestClient apolloTestClient, Environment environment) throws Exception {

        ApolloTestAdminClient apolloTestAdminClient = Common.getAndLoginApolloTestAdminClient();

        DeploymentGroup newDeploymentGroup = ModelsGenerator.createDeploymentGroup();
        newDeploymentGroup.setId(apolloTestAdminClient.addDeploymentGroup(newDeploymentGroup).getId());

        DeploymentPermission newDeploymentPermission = ModelsGenerator.createAllowDeploymentPermission(Optional.of(environment), Optional.empty());
        newDeploymentPermission.setId(apolloTestAdminClient.addDeploymentPermission(newDeploymentPermission).getId());

        DeploymentGroupPermission newDeploymentGroupPermission = ModelsGenerator.createGroupPermission(newDeploymentGroup, newDeploymentPermission);
        apolloTestAdminClient.addGroupPermission(newDeploymentGroupPermission);

        DeploymentUserGroup newDeploymentUserGroup = ModelsGenerator.createUserGroup(apolloTestClient.getClientUser(), newDeploymentGroup);
        apolloTestAdminClient.addUserGroup(newDeploymentUserGroup);
    }
}
