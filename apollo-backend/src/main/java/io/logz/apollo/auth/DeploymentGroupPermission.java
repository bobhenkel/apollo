package io.logz.apollo.auth;

/**
 * Created by roiravhon on 1/10/17.
 */
public class DeploymentGroupPermission {

    private int deploymentGroupId;
    private int deploymentPermissionId;

    public DeploymentGroupPermission() {

    }

    public int getDeploymentGroupId() {
        return deploymentGroupId;
    }

    public void setDeploymentGroupId(int deploymentGroupId) {
        this.deploymentGroupId = deploymentGroupId;
    }

    public int getDeploymentPermissionId() {
        return deploymentPermissionId;
    }

    public void setDeploymentPermissionId(int deploymentPermissionId) {
        this.deploymentPermissionId = deploymentPermissionId;
    }
}
