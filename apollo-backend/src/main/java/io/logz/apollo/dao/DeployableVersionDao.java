package io.logz.apollo.dao;

import io.logz.apollo.models.DeployableVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by roiravhon on 12/20/16.
 */
public interface DeployableVersionDao {

    DeployableVersion getDeployableVersion(int id);
    List<DeployableVersion> getAllDeployableVersions();
    DeployableVersion getDeployableVersionFromSha(@Param("gitCommitSha") String gitCommitSha);
    void addDeployableVersion(DeployableVersion deployableVersion);
}
