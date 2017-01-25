package io.logz.apollo.auth;

/**
 * Created by roiravhon on 1/10/17.
 */
public class DeploymentUserGroup {

    private String userEmail;
    private int deploymentGroupId;

    public DeploymentUserGroup() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getDeploymentGroupId() {
        return deploymentGroupId;
    }

    public void setDeploymentGroupId(int deploymentGroupId) {
        this.deploymentGroupId = deploymentGroupId;
    }
}
