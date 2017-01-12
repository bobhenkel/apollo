package io.logz.apollo.dao;

import io.logz.apollo.auth.GroupPermission;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface GroupPermissionDao {

    List<GroupPermission> getAllGroupPermissionsByGroup(int groupId);
    List<GroupPermission> getAllGroupPermissions();
    void addGroupPermission(GroupPermission groupPermission);
}