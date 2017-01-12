package io.logz.apollo.dao;

import io.logz.apollo.auth.Permission;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface PermissionDao {

    Permission getPermission(int id);
    List<Permission> getAllPermissions();
    void addPermission(Permission permission);
}
