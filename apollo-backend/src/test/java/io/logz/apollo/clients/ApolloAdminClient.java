package io.logz.apollo.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import io.logz.apollo.auth.Group;
import io.logz.apollo.auth.GroupPermission;
import io.logz.apollo.auth.Permission;
import io.logz.apollo.auth.User;
import io.logz.apollo.auth.UserGroup;
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

    public Group addGroup(Group group) throws ApolloClientException {
        String requestBody = Common.generateJson("name", group.getName());
        return genericApolloClient.postAndGetResult("/group", requestBody, new TypeReference<Group>() {});
    }

    public Group getGroup(int id) throws ApolloClientException {
        return genericApolloClient.getResult("/group/" + id, new TypeReference<Group>() {});
    }

    public List<Group> getAllGroups() throws ApolloClientException {
        return genericApolloClient.getResult("/group", new TypeReference<List<Group>>() {});
    }

    public Permission addPermission(Permission permission) throws ApolloClientException {
        String requestBody = Common.generateJson("name", permission.getName(),
                "serviceId", String.valueOf(permission.getServiceId()),
                "environmentId", String.valueOf(permission.getEnvironmentId()),
                "permissionType", permission.getPermissionType().toString());

        return genericApolloClient.postAndGetResult("/permission", requestBody, new TypeReference<Permission>() {});
    }

    public Permission getPermission(int id) throws ApolloClientException {
        return genericApolloClient.getResult("/permission", new TypeReference<Permission>() {});
    }

    public List<Permission> getAllPermissions() throws ApolloClientException {
        return genericApolloClient.getResult("/permission", new TypeReference<List<Permission>>() {});
    }

    public UserGroup addUserGroup(UserGroup userGroup) throws ApolloClientException {
        String requestBody = Common.generateJson("userEmail", userGroup.getUserEmail(),
                "groupId", String.valueOf(userGroup.getGroupId()));

        return genericApolloClient.postAndGetResult("/user-group", requestBody, new TypeReference<UserGroup>() {});
    }

    public List<UserGroup> getAllUserGroupsByUser(String userEmail) throws ApolloClientException {
        return genericApolloClient.getResult("/user-group/" + userEmail, new TypeReference<List<UserGroup>>() {});
    }

    public List<UserGroup> getAllUserGroups() throws ApolloClientException {
        return genericApolloClient.getResult("/user-group", new TypeReference<List<UserGroup>>() {});
    }

    public GroupPermission addGroupPermission(GroupPermission groupPermission) throws ApolloClientException {
        String requestBody = Common.generateJson("groupId", String.valueOf(groupPermission.getGroupId()),
                "permissionId", String.valueOf(groupPermission.getPermissionId()));

        return genericApolloClient.postAndGetResult("/group-permission", requestBody, new TypeReference<GroupPermission>() {});
    }

    public List<GroupPermission> getAllGroupPermissionsByGroup(int groupId) throws ApolloClientException {
        return genericApolloClient.getResult("/group-permission/" + groupId, new TypeReference<List<GroupPermission>>() {});
    }

    public List<GroupPermission> getAllGroupPermissions() throws ApolloClientException {
        return genericApolloClient.getResult("/group-permission", new TypeReference<List<GroupPermission>>() {});
    }

    private String generateSignupJson(User user, String plainPassword) {
        return Common.generateJson( "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "userEmail", user.getUserEmail(),
                                    "password", plainPassword);
    }
}
