package io.logz.apollo.dao;

import io.logz.apollo.auth.DeploymentPermission;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentPermissionDao {

    DeploymentPermission getDeploymentPermission(int id);
    List<DeploymentPermission> getAllDeploymentPermissions();
    void addDeploymentPermission(DeploymentPermission deploymentPermission);
}
