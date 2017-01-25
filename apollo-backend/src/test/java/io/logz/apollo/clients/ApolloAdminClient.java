package io.logz.apollo.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.DeploymentGroupPermission;
import io.logz.apollo.auth.User;
import io.logz.apollo.auth.DeploymentUserGroup;
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

    public DeploymentUserGroup addUserGroup(DeploymentUserGroup deploymentUserGroup) throws ApolloClientException {
        String requestBody = Common.generateJson("userEmail", deploymentUserGroup.getUserEmail(),
                "deploymentGroupId", String.valueOf(deploymentUserGroup.getDeploymentGroupId()));

        return genericApolloClient.postAndGetResult("/deployment-user-group", requestBody, new TypeReference<DeploymentUserGroup>() {});
    }

    public List<DeploymentUserGroup> getAllUserGroupsByUser(String userEmail) throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-user-group/" + userEmail, new TypeReference<List<DeploymentUserGroup>>() {});
    }

    public List<DeploymentUserGroup> getAllUserGroups() throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-user-group", new TypeReference<List<DeploymentUserGroup>>() {});
    }

    public DeploymentGroupPermission addGroupPermission(DeploymentGroupPermission deploymentGroupPermission) throws ApolloClientException {
        String requestBody = Common.generateJson("deploymentGroupId", String.valueOf(deploymentGroupPermission.getDeploymentGroupId()),
                "deploymentPermissionId", String.valueOf(deploymentGroupPermission.getDeploymentPermissionId()));

        return genericApolloClient.postAndGetResult("/deployment-group-permission", requestBody, new TypeReference<DeploymentGroupPermission>() {});
    }

    public List<DeploymentGroupPermission> getAllGroupPermissionsByGroup(int groupId) throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-group-permission/" + groupId, new TypeReference<List<DeploymentGroupPermission>>() {});
    }

    public List<DeploymentGroupPermission> getAllGroupPermissions() throws ApolloClientException {
        return genericApolloClient.getResult("/deployment-group-permission", new TypeReference<List<DeploymentGroupPermission>>() {});
    }

    private String generateSignupJson(User user, String plainPassword) {
        return Common.generateJson( "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "userEmail", user.getUserEmail(),
                                    "password", plainPassword);
    }
}
