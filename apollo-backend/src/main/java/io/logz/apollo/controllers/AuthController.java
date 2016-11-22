package io.logz.apollo.controllers;

import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.User;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.security.Role;
import org.rapidoid.security.annotation.Administrator;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.setup.My;
import org.rapidoid.u.U;

import java.util.List;

/**
 * Created by roiravhon on 11/21/16.
 */
@Controller
public class AuthController {

    private final UserDao userDao;

    public AuthController() {
        userDao = ApolloMyBatis.getDao(UserDao.class);
        initializeLoginProvider();
        initializeRolesProvider();
    }

    @LoggedIn
    @GET("/users")
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Administrator
    @POST("/signup")
    public User addUser(String emailAddress, String firstName, String lastName, String password) {

        User newUser = new User(emailAddress, firstName, lastName, PasswordManager.encryptPassword(password), false);
        userDao.addUser(newUser);

        return newUser;
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
