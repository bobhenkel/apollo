package io.logz.apollo.models;

/**
 * Created by roiravhon on 12/20/16.
 */
public class Service {

    private int id;
    private String name;
    private String deploymentYaml;
    private String serviceYaml;

    public Service() {

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

    public String getDeploymentYaml() {
        return deploymentYaml;
    }

    public void setDeploymentYaml(String deploymentYaml) {
        this.deploymentYaml = deploymentYaml;
    }

    public String getServiceYaml() {
        return serviceYaml;
    }

    public void setServiceYaml(String serviceYaml) {
        this.serviceYaml = serviceYaml;
    }
}
