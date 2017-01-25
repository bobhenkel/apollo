package io.logz.apollo.dao;

import io.logz.apollo.auth.DeploymentGroup;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentGroupDao {

    DeploymentGroup getDeploymentGroup(int id);
    List<DeploymentGroup> getAllDeploymentGroups();
    void addDeploymentGroup(DeploymentGroup deploymentGroup);
}
