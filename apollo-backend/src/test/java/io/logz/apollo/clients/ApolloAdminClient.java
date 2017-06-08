package io.logz.apollo.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.User;
import io.logz.apollo.blockers.BlockerDefinition;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.exceptions.ApolloCouldNotLoginException;
import io.logz.apollo.exceptions.ApolloCouldNotSignupException;
import io.logz.apollo.exceptions.ApolloNotAuthenticatedException;
import io.logz.apollo.exceptions.ApolloNotAuthorizedException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.RestResponse;

import java.io.IOException;
import java.util.List;

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

    public DeploymentGroup addDeploymentGroup(DeploymentGroup deploymentGroup) throws ApolloClientException {
        String requestBody = Common.generateJson("name", deploymentGroup.getName());
        return genericApolloClient.postAndGetResult("/deployment-group", requestBody, new TypeReference<DeploymentGroup>() {});
    }

    public DeploymentGroup getDeploymentGroup(int id) throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-group/" + id, new TypeReference<DeploymentGroup>() {});
    }

    public List<DeploymentGroup> getAllDeploymentGroups() throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-group", new TypeReference<List<DeploymentGroup>>() {});
    }

    public DeploymentPermission addDeploymentPermission(DeploymentPermission deploymentPermission) throws ApolloClientException {
        String requestBody = Common.generateJson("name", deploymentPermission.getName(),
                "serviceId", String.valueOf(deploymentPermission.getServiceId()),
                "environmentId", String.valueOf(deploymentPermission.getEnvironmentId()),
                "permissionType", deploymentPermission.getPermissionType().toString());

        return genericApolloClient.postAndGetResult("/deployment-permission", requestBody, new TypeReference<DeploymentPermission>() {});
    }

    public DeploymentPermission getDeploymentPermission(int id) throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-permission", new TypeReference<DeploymentPermission>() {});
    }

    public List<DeploymentPermission> getAllDeploymentPermissions() throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-permission", new TypeReference<List<DeploymentPermission>>() {});
    }

    public void addUserToGroup(String userEmail, int deploymentGroupId) throws ApolloClientException {
        String requestBody = Common.generateJson("userEmail", userEmail,
                "deploymentGroupId", String.valueOf(deploymentGroupId));

        genericApolloClient.postAndGetResult("/add-user-to-deployment-group", requestBody, new TypeReference<Object>() {});
    }

    public void addDeploymentPermissionToDeploymentGroup(int deploymentGroupId, int deploymentPermissionId) throws ApolloClientException {
        String requestBody = Common.generateJson("deploymentGroupId", String.valueOf(deploymentGroupId),
                "deploymentPermissionId", String.valueOf(deploymentPermissionId));

        genericApolloClient.postAndGetResult("/add-deployment-permission-to-deployment-group", requestBody, new TypeReference<Object>() {});
    }

    public BlockerDefinition addBlocker(BlockerDefinition blockerDefinition) throws ApolloClientException {
        String requestBody = Common.generateJson("name", blockerDefinition.getName(),
                "environmentId", String.valueOf(blockerDefinition.getEnvironmentId()),
                "serviceId", String.valueOf(blockerDefinition.getServiceId()),
                "isActive", String.valueOf(blockerDefinition.getActive()),
                "blockerTypeName", blockerDefinition.getBlockerTypeName(),
                "blockerJsonConfiguration", blockerDefinition.getBlockerJsonConfiguration());

        return genericApolloClient.postAndGetResult("/blocker-definition", requestBody, new TypeReference<BlockerDefinition>() {});
    }

    public BlockerDefinition updateBlocker(BlockerDefinition blockerDefinition) throws ApolloClientException {
        String requestBody = Common.generateJson("id", String.valueOf(blockerDefinition.getId()),
                "name", blockerDefinition.getName(),
                "environmentId", String.valueOf(blockerDefinition.getEnvironmentId()),
                "serviceId", String.valueOf(blockerDefinition.getServiceId()),
                "isActive", String.valueOf(blockerDefinition.getActive()),
                "blockerTypeName", blockerDefinition.getBlockerTypeName(),
                "blockerJsonConfiguration", blockerDefinition.getBlockerJsonConfiguration());

        return genericApolloClient.putAndGetResult("/blocker-definition/" + blockerDefinition.getId(), requestBody, new TypeReference<BlockerDefinition>() {});
    }

    private String generateSignupJson(User user, String plainPassword) {
        return Common.generateJson( "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "userEmail", user.getUserEmail(),
                                    "password", plainPassword);
    }
}
