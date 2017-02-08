package io.logz.apollo.dao;

import io.logz.apollo.models.Deployment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by roiravhon on 1/5/17.
 */
public interface DeploymentDao {

    Deployment getDeployment(int id);
    List<Deployment> getAllDeployments();
    List<Deployment> getAllRunningDeployments();
    void addDeployment(Deployment deployment);
    void updateDeploymentStatus(@Param("id") int id, @Param("status") Deployment.DeploymentStatus status);
}
