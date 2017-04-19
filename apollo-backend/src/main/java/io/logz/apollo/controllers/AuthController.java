package io.logz.apollo.controllers;

import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.User;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.DeploymentGroupDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.Role;
import org.rapidoid.security.annotation.Administrator;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;

/**
 * Created by roiravhon on 11/21/16.
 */
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController() {
        initializeLoginProvider();
        initializeRolesProvider();
    }

    @LoggedIn
    @GET("/user")
    public List<User> getAllUsers() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            UserDao userDao = apolloMyBatisSession.getDao(UserDao.class);
            return userDao.getAllUsers().stream().map(user -> {
                user.setHashedPassword("******");
                return user;
            }).collect(Collectors.toList());
        }
    }

    @Administrator
    @POST("/signup")
    public User addUser(String userEmail, String firstName, String lastName, String password) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            UserDao userDao = apolloMyBatisSession.getDao(UserDao.class);

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
    }

    @Administrator
    @GET("/deployment-group")
    public List<DeploymentGroup> getAllDeploymentGroups(Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentGroupDao deploymentGroupDao = apolloMyBatisSession.getDao(DeploymentGroupDao.class);
            return deploymentGroupDao.getAllDeploymentGroups();
        }
    }

    @Administrator
    @GET("/deployment-group/{id}")
    public DeploymentGroup getDeploymentGroup(int id, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentGroupDao deploymentGroupDao = apolloMyBatisSession.getDao(DeploymentGroupDao.class);
            return deploymentGroupDao.getDeploymentGroup(id);
        }
    }

    @Administrator
    @POST("/deployment-group")
    public void addDeploymentGroup(String name, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentGroupDao deploymentGroupDao = apolloMyBatisSession.getDao(DeploymentGroupDao.class);
            DeploymentGroup newDeploymentGroup = new DeploymentGroup();
            newDeploymentGroup.setName(name);

            deploymentGroupDao.addDeploymentGroup(newDeploymentGroup);
            assignJsonResponseToReq(req, HttpStatus.CREATED, newDeploymentGroup);
        }
    }

    @Administrator
    @GET("/deployment-permission")
    public List<DeploymentPermission> getAllDeploymentPermissions(Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentPermissionDao deploymentPermissionDao = apolloMyBatisSession.getDao(DeploymentPermissionDao.class);
            return deploymentPermissionDao.getAllDeploymentPermissions();
        }
    }

    @Administrator
    @GET("/deployment-permission/{id}")
    public DeploymentPermission getDeploymentPermission(int id, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentPermissionDao deploymentPermissionDao = apolloMyBatisSession.getDao(DeploymentPermissionDao.class);
            return deploymentPermissionDao.getDeploymentPermission(id);
        }
    }

    @Administrator
    @POST("/deployment-permission")
    public void addDeploymentPermission(String name, String serviceId, String  environmentId, DeploymentPermission.PermissionType permissionType, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentPermissionDao deploymentPermissionDao = apolloMyBatisSession.getDao(DeploymentPermissionDao.class);
            DeploymentPermission newDeploymentPermission = new DeploymentPermission();
            newDeploymentPermission.setName(name);

            // This is how rapidoid represent nulls. also has to be string to not have a NumberFormatException
            if (serviceId.equals("null") && environmentId.equals("null")) {
                req.response().code(HttpStatus.BAD_REQUEST);
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
            assignJsonResponseToReq(req, HttpStatus.CREATED, newDeploymentPermission);
        }
    }

    @Administrator
    @POST("/add-user-to-deployment-group")
    public void addUserToDeploymentGroup(String userEmail, int deploymentGroupId, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentGroupDao deploymentGroupDao = apolloMyBatisSession.getDao(DeploymentGroupDao.class);
            deploymentGroupDao.addUserToDeploymentGroup(userEmail, deploymentGroupId);
            assignJsonResponseToReq(req, HttpStatus.CREATED, "ok");
        }
    }

    @Administrator
    @POST("/add-deployment-permission-to-deployment-group")
    public void addDeploymentPermissionToDeploymentGroup(int deploymentGroupId, int deploymentPermissionId, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentGroupDao deploymentGroupDao = apolloMyBatisSession.getDao(DeploymentGroupDao.class);
            deploymentGroupDao.addDeploymentPermissionToDeploymentGroup(deploymentGroupId, deploymentPermissionId);
            assignJsonResponseToReq(req, HttpStatus.CREATED, "ok");
        }
    }

    private void initializeLoginProvider() {
        My.loginProvider((req, username, password) -> {
            try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
                UserDao userDao = apolloMyBatisSession.getDao(UserDao.class);
                User requestedUser = userDao.getUser(username);
                if (requestedUser == null) {
                    req.response().code(HttpStatus.UNAUTHORIZED);
                    return false;
                }
                if (PasswordManager.checkPassword(password, requestedUser.getHashedPassword())) {
                    return true;
                } else {
                    req.response().code(HttpStatus.UNAUTHORIZED);
                    return false;
                }
            }
        });
    }

    private void initializeRolesProvider() {
        My.rolesProvider((req, username) -> {
            try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
                UserDao userDao = apolloMyBatisSession.getDao(UserDao.class);

                if (userDao.getUser(username).isAdmin()) {
                    return U.set(Role.ADMINISTRATOR);
                }
                return U.set(Role.ANYBODY);

            } catch (Exception e) {
                logger.error("Got exception while getting user roles! setting to ANYBODY", e);
                return U.set(Role.ANYBODY);
            }
        });
    }
}
