package io.logz.apollo.dao;

import io.logz.apollo.models.Deployment;

import java.util.List;

/**
 * Created by roiravhon on 1/5/17.
 */
public interface DeploymentDao {

    Deployment getDeployment(int id);
    List<Deployment> getAllDeployments();
    void addDeployment(Deployment deployment);
    void updateDeploymentStatus(int id, Deployment.DeploymentStatus status);
}
