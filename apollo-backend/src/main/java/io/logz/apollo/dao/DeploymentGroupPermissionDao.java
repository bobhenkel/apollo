package io.logz.apollo.dao;

import io.logz.apollo.auth.DeploymentGroupPermission;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentGroupPermissionDao {

    List<DeploymentGroupPermission> getAllDeploymentGroupPermissionsByGroup(int groupId);
    List<DeploymentGroupPermission> getAllDeploymentGroupPermissions();
    void addDeploymentGroupPermission(DeploymentGroupPermission deploymentGroupPermission);
}