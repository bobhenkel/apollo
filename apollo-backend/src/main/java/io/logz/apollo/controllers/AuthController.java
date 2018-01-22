package io.logz.apollo.controllers;

import io.logz.apollo.models.DeploymentGroup;
import io.logz.apollo.models.DeploymentPermission;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.models.User;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.DeploymentGroupDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.UserDao;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.PUT;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.Administrator;
import org.rapidoid.security.annotation.LoggedIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 11/21/16.
 */
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final DeploymentPermissionDao deploymentPermissionDao;
    private final DeploymentGroupDao deploymentGroupDao;
    private final UserDao userDao;

    @Inject
    public AuthController(DeploymentPermissionDao deploymentPermissionDao,
                          DeploymentGroupDao deploymentGroupDao,
                          UserDao userDao) {
        this.deploymentPermissionDao = requireNonNull(deploymentPermissionDao);
        this.deploymentGroupDao = requireNonNull(deploymentGroupDao);
        this.userDao = requireNonNull(userDao);
    }

    @LoggedIn
    @GET("/user")
    public List<User> getAllUsers() {
        return userDao.getAllUsers().stream()
                .map(this::maskPassword)
                .collect(Collectors.toList());
    }

    @LoggedIn
    @GET("/user/{email}")
    public User getUser(String email) { return userDao.getUser(email); }


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
    @PUT("/user")
    public User updateUser(String userEmail, String firstName, String lastName, String password, Boolean isAdmin) {
        User user = getUser(userEmail);
        user.setUserEmail(userEmail);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setHashedPassword(PasswordManager.encryptPassword(password));
        user.setAdmin(isAdmin);
        userDao.updateUser(user);

        return user;
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
        assignJsonResponseToReq(req, HttpStatus.CREATED, newDeploymentGroup);
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

    @Administrator
    @POST("/notify-user-to-deployment-group")
    public void addUserToDeploymentGroup(String userEmail, int deploymentGroupId, Req req) {
        deploymentGroupDao.addUserToDeploymentGroup(userEmail, deploymentGroupId);
        assignJsonResponseToReq(req, HttpStatus.CREATED, "ok");
    }

    @Administrator
    @POST("/notify-deployment-permission-to-deployment-group")
    public void addDeploymentPermissionToDeploymentGroup(int deploymentGroupId, int deploymentPermissionId, Req req) {
        deploymentGroupDao.addDeploymentPermissionToDeploymentGroup(deploymentGroupId, deploymentPermissionId);
        assignJsonResponseToReq(req, HttpStatus.CREATED, "ok");
    }

    private User maskPassword(User user) {
        user.setHashedPassword("******");
        return user;
    }

}
