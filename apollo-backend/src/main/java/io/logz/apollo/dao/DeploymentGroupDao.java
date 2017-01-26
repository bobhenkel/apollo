package io.logz.apollo.dao;

import io.logz.apollo.auth.DeploymentGroup;
import org.apache.ibatis.annotations.Param;
import org.rapidoid.annotation.PATCH;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentGroupDao {

    DeploymentGroup getDeploymentGroup(int id);
    List<DeploymentGroup> getAllDeploymentGroups();
    void addDeploymentGroup(DeploymentGroup deploymentGroup);
    void addUserToDeploymentGroup(@Param("userEmail") String userEmail, @Param("deploymentGroupId") int deploymentGroupId);
    void addDeploymentPermissionToDeploymentGroup(@Param("deploymentGroupId") int deploymentGroupId, @Param("deploymentPermissionId") int deploymentPermissionId);
}
