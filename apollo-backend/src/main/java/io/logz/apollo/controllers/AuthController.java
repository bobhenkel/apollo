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
import java.util.stream.Collectors;

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
