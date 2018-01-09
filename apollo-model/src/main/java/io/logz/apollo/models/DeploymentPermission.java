package io.logz.apollo.models;

/**
 * Created by roiravhon on 1/10/17.
 */
public class DeploymentPermission {

    public enum PermissionType {
        ALLOW,
        DENY
    }

    private int id;
    private String name;
    private Integer serviceId;
    private Integer environmentId;
    private PermissionType permissionType;

    public DeploymentPermission() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Integer environmentId) {
        this.environmentId = environmentId;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
