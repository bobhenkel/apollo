package io.logz.apollo.models;

/**
 * Created by roiravhon on 1/10/17.
 */
public class DeploymentGroup {

    private int id;
    private String name;

    public DeploymentGroup() {

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
}
