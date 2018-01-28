package io.logz.apollo.dao;

import io.logz.apollo.models.DeploymentRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by roiravhon on 1/10/17.
 */
public interface DeploymentRoleDao {

    DeploymentRole getDeploymentRole(int id);
    List<DeploymentRole> getAllDeploymentRoles();
    void addDeploymentRole(DeploymentRole deploymentRole);
    void addUserToDeploymentRole(@Param("userEmail") String userEmail, @Param("deploymentRoleId") int deploymentRoleId);
    void addDeploymentPermissionToDeploymentRole(@Param("deploymentRoleId") int deploymentRoleId, @Param("deploymentPermissionId") int deploymentPermissionId);
}
