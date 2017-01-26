package io.logz.apollo.controllers;

import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.User;
import io.logz.apollo.dao.DeploymentGroupDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.security.Role;
import org.rapidoid.security.annotation.Administrator;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by roiravhon on 11/21/16.
 */
@Controller
public class AuthController {

    private final UserDao userDao;
    private final DeploymentGroupDao deploymentGroupDao;
    private final DeploymentPermissionDao deploymentPermissionDao;

    public AuthController() {
        userDao = ApolloMyBatis.getDao(UserDao.class);
        deploymentGroupDao = ApolloMyBatis.getDao(DeploymentGroupDao.class);
        deploymentPermissionDao = ApolloMyBatis.getDao(DeploymentPermissionDao.class);
        initializeLoginProvider();
        initializeRolesProvider();
    }

    @LoggedIn
    @GET("/user")
    public List<User> getAllUsers() {
        return userDao.getAllUsers().stream().map(user -> {
            user.setHashedPassword("******");
            return user;
        }).collect(Collectors.toList());
    }

    @Administrator
    @POST("/signup")
    public User addUser(String userEmail, String firstName, String lastName, String password) {

        // TODO: validate input
        User newUser = new User();
        newUser.setUserEmail(userEmail);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setHashedPassword(PasswordManager.encryptPassword(password));
        newUser.setAdmin(false);
        userDao.addUser(newUser);

        return newUser;
    }

    @Administrator
    @GET("/deployment-group")
    public List<DeploymentGroup> getAllDeploymentGroups(Req req) {
        return deploymentGroupDao.getAllDeploymentGroups();
    }

    @Administrator
    @GET("/deployment-group/{id}")
    public DeploymentGroup getDeploymentGroup(int id, Req req) {
        return deploymentGroupDao.getDeploymentGroup(id);
    }

    @Administrator
    @POST("/deployment-group")
    public void addDeploymentGroup(String name, Req req) {
        DeploymentGroup newDeploymentGroup = new DeploymentGroup();
        newDeploymentGroup.setName(name);

        deploymentGroupDao.addDeploymentGroup(newDeploymentGroup);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newDeploymentGroup);
    }

    @Administrator
    @GET("/deployment-permission")
    public List<DeploymentPermission> getAllDeploymentPermissions(Req req) {
        return deploymentPermissionDao.getAllDeploymentPermissions();
    }

    @Administrator
    @GET("/deployment-permission/{id}")
    public DeploymentPermission getDeploymentPermission(int id, Req req) {
        return deploymentPermissionDao.getDeploymentPermission(id);
    }

    @Administrator
    @POST("/deployment-permission")
    public void addDeploymentPermission(String name, String serviceId, String  environmentId, DeploymentPermission.PermissionType permissionType, Req req) {
        DeploymentPermission newDeploymentPermission = new DeploymentPermission();
        newDeploymentPermission.setName(name);

        // This is how rapidoid represent nulls. also has to be string to not have a NumberFormatException
        if (serviceId.equals("null") && environmentId.equals("null")) {
            req.response().code(400);
            req.response().json("One of serviceId or environmentId must be present!");
        } else {
            if (!serviceId.equals("null")) {
                newDeploymentPermission.setServiceId(Integer.parseInt(serviceId));
            }
            if (!environmentId.equals("null")) {
                newDeploymentPermission.setEnvironmentId(Integer.parseInt(environmentId));
            }
        }

        newDeploymentPermission.setPermissionType(permissionType);

        deploymentPermissionDao.addDeploymentPermission(newDeploymentPermission);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newDeploymentPermission);
    }

    @Administrator
    @POST("/add-user-to-deployment-group")
    public void addUserToDeploymentGroup(String userEmail, int deploymentGroupId, Req req) {
        deploymentGroupDao.addUserToDeploymentGroup(userEmail, deploymentGroupId);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json("ok");
    }

    @Administrator
    @POST("/add-deployment-permission-to-deployment-group")
    public void addDeploymentPermissionToDeploymentGroup(int deploymentGroupId, int deploymentPermissionId, Req req) {

        deploymentGroupDao.addDeploymentPermissionToDeploymentGroup(deploymentGroupId, deploymentPermissionId);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json("ok");
    }

    private void initializeLoginProvider() {
        My.loginProvider((req, username, password) -> {
            User requestedUser = userDao.getUser(username);
            if (requestedUser == null) {
                return false;
            }
            return PasswordManager.checkPassword(password, requestedUser.getHashedPassword());
        });
    }

    private void initializeRolesProvider() {
        My.rolesProvider((req, username) -> {
            if (userDao.getUser(username).isAdmin()) {
                return U.set(Role.ADMINISTRATOR);
            }
            return U.set(Role.ANYBODY);
        });
    }
}
