package io.logz.apollo.dao;

import io.logz.apollo.auth.User;

import java.util.List;

/**
 * Created by roiravhon on 11/20/16.
 */
public interface UserDao {

    User getUser(String emailAddress);
    List<User> getAllUsers();
    void addUser(User user);
}
