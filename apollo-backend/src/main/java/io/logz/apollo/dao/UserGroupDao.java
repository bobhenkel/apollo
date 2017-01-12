package io.logz.apollo.dao;

import io.logz.apollo.auth.UserGroup;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface UserGroupDao {

    List<UserGroup> getAllUserGroupsByUser(String userEmail);
    List<UserGroup> getAllUserGroups();
    void addUserGroup(UserGroup userGroup);
}
