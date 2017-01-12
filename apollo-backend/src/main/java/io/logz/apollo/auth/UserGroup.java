package io.logz.apollo.auth;

/**
 * Created by roiravhon on 1/10/17.
 */
public class UserGroup {

    private String userEmail;
    private int groupId;

    public UserGroup() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
