package io.logz.apollo.dao;

import io.logz.apollo.models.DeploymentPermission;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentPermissionDao {

    DeploymentPermission getDeploymentPermission(int id);
    List<DeploymentPermission> getAllDeploymentPermissions();
    List<DeploymentPermission> getPermissionsByUser(String userEmail);
    void addDeploymentPermission(DeploymentPermission deploymentPermission);
}
