package io.logz.apollo.auth;

/**
 * Created by roiravhon on 1/10/17.
 */
public class GroupPermission {

    private int groupId;
    private int permissionId;

    public GroupPermission() {

    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }
}
