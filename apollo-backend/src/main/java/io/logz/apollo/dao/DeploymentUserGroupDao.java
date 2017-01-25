package io.logz.apollo.dao;

import io.logz.apollo.auth.DeploymentUserGroup;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentUserGroupDao {

    List<DeploymentUserGroup> getAllDeploymentUserGroupsByUser(String userEmail);
    List<DeploymentUserGroup> getAllDeploymentUserGroups();
    void addDeploymentUserGroup(DeploymentUserGroup deploymentUserGroup);
}
