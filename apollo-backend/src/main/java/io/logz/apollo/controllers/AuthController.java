package io.logz.apollo.controllers;

import io.logz.apollo.auth.Group;
import io.logz.apollo.auth.GroupPermission;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.Permission;
import io.logz.apollo.auth.User;
import io.logz.apollo.auth.UserGroup;
import io.logz.apollo.dao.GroupDao;
import io.logz.apollo.dao.GroupPermissionDao;
import io.logz.apollo.dao.PermissionDao;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.dao.UserGroupDao;
import io.logz.apollo.database.ApolloMyBatis;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.Param;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.security.Role;
import org.rapidoid.security.annotation.Administrator;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by roiravhon on 11/21/16.
 */
@Controller
public class AuthController {

    private final UserDao userDao;
    private final GroupDao groupDao;
    private final PermissionDao permissionDao;
    private final GroupPermissionDao groupPermissionDao;
    private final UserGroupDao userGroupDao;

    public AuthController() {
        userDao = ApolloMyBatis.getDao(UserDao.class);
        groupDao = ApolloMyBatis.getDao(GroupDao.class);
        permissionDao = ApolloMyBatis.getDao(PermissionDao.class);
        groupPermissionDao = ApolloMyBatis.getDao(GroupPermissionDao.class);
        userGroupDao = ApolloMyBatis.getDao(UserGroupDao.class);
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
    @GET("/group")
    public List<Group> getAllGroups(Req req) {
        return groupDao.getAllGroups();
    }

    @Administrator
    @GET("/group/{id}")
    public Group getGroup(int id, Req req) {
        return groupDao.getGroup(id);
    }

    @Administrator
    @POST("/group")
    public void addGroup(String name, Req req) {
        Group newGroup = new Group();
        newGroup.setName(name);

        groupDao.addGroup(newGroup);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newGroup);
    }

    @Administrator
    @GET("/permission")
    public List<Permission> getAllPermissions(Req req) {
        return permissionDao.getAllPermissions();
    }

    @Administrator
    @GET("/permission/{id}")
    public Permission getPermission(int id, Req req) {
        return permissionDao.getPermission(id);
    }

    @Administrator
    @POST("/permission")
    public void addPermission(String name, String serviceId, String  environmentId, Permission.PermissionType permissionType, Req req) {
        Permission newPermission = new Permission();
        newPermission.setName(name);

        // This is how rapidoid represent nulls. also has to be string to not have a NumberFormatException
        if (serviceId.equals("null") && environmentId.equals("null")) {
            req.response().code(400);
            req.response().json("One of serviceId or environmentId must be present!");
        } else {
            if (!serviceId.equals("null")) {
                newPermission.setServiceId(Integer.parseInt(serviceId));
            }
            if (!environmentId.equals("null")) {
                newPermission.setEnvironmentId(Integer.parseInt(environmentId));
            }
        }

        newPermission.setPermissionType(permissionType);

        permissionDao.addPermission(newPermission);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newPermission);
    }

    @Administrator
    @GET("/user-group")
    public List<UserGroup> getAllUserGroups(Req req) {
        return userGroupDao.getAllUserGroups();
    }

    @Administrator
    @GET("/user-group/{userEmail}")
    public List<UserGroup> getAllUserGroupsByUser(String userEmail, Req req) {
        return userGroupDao.getAllUserGroupsByUser(userEmail);
    }

    @Administrator
    @POST("/user-group")
    public void addUserGroup(String userEmail, int groupId, Req req) {
        UserGroup newUserGroup = new UserGroup();
        newUserGroup.setUserEmail(userEmail);
        newUserGroup.setGroupId(groupId);

        userGroupDao.addUserGroup(newUserGroup);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newUserGroup);
    }

    @Administrator
    @GET("/group-permission")
    public List<GroupPermission> getAllGroupPermissions(Req req) {
        return groupPermissionDao.getAllGroupPermissions();
    }

    @Administrator
    @GET("/group-permission/{id}")
    public List<GroupPermission> getAllGroupPermissionsByGroup(int id, Req req) {
        return groupPermissionDao.getAllGroupPermissionsByGroup(id);
    }

    @Administrator
    @POST("/group-permission")
    public void addGroupPermission(int groupId, int permissionId, Req req) {
        GroupPermission newGroupPermission = new GroupPermission();
        newGroupPermission.setGroupId(groupId);
        newGroupPermission.setPermissionId(permissionId);

        groupPermissionDao.addGroupPermission(newGroupPermission);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newGroupPermission);
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
