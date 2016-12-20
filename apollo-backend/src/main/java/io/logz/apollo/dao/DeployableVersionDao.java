package io.logz.apollo.dao;

import io.logz.apollo.models.DeployableVersion;

import java.util.List;

/**
 * Created by roiravhon on 12/20/16.
 */
public interface DeployableVersionDao {

    DeployableVersion getDeployableVersion(int id);
    List<DeployableVersion> getAllDeployableVersions();
    void addDeployableVersion(DeployableVersion deployableVersion);
}
