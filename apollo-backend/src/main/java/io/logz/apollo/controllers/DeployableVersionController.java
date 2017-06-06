package io.logz.apollo.controllers;

import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.scm.GithubConnector;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static io.logz.apollo.scm.GithubConnector.getRepoNameFromRepositoryUrl;
import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 12/20/16.
 */
@Controller
public class DeployableVersionController {

    private final DeployableVersionDao deployableVersionDao;
    private final GithubConnector githubConnector;

    @Inject
    public DeployableVersionController(DeployableVersionDao deployableVersionDao, GithubConnector githubConnector) {
        this.deployableVersionDao = requireNonNull(deployableVersionDao);
        this.githubConnector = requireNonNull(githubConnector);
    }

    @LoggedIn
    @GET("/deployable-version")
    public List<DeployableVersion> getAllDeployableVersion() {
        return deployableVersionDao.getAllDeployableVersions();
    }

    @LoggedIn
    @GET("/deployable-version/{id}")
    public DeployableVersion getDeployableVersion(int id) {
        return deployableVersionDao.getDeployableVersion(id);
    }

    @LoggedIn
    @GET("/deployable-version/sha/{sha}/service/{serviceId}")
    public DeployableVersion getDeployableVersionFromSha(String sha, int serviceId) {
        return deployableVersionDao.getDeployableVersionFromSha(sha, serviceId);
    }

    @LoggedIn
    @GET("/deployable-version/latest/service/{serviceId}")
    public List<DeployableVersion> getLatestDeployableVersionsByServiceId(int serviceId) {
        return deployableVersionDao.getLatestDeployableVersionsByServiceId(serviceId);
    }

    @LoggedIn
    @GET("/deployable-version/latest/branch/{branchName}/repofrom/{deployableVersionId}")
    public DeployableVersion getLatestDeployableVersionOnBranchBasedOnOtherDeployableVersion(String branchName, int deployableVersionId, Req req) {
        DeployableVersion referenceDeployableVersion = deployableVersionDao.getDeployableVersion(deployableVersionId);
        String actualRepo = getRepoNameFromRepositoryUrl(referenceDeployableVersion.getGithubRepositoryUrl());

        Optional<String> latestSha = githubConnector.getLatestCommitShaOnBranch(actualRepo, branchName);

        if (!latestSha.isPresent()) {
            assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Did not found latest commit on that branch");
            throw new RuntimeException();
        }

        DeployableVersion deployableVersionFromSha = deployableVersionDao.getDeployableVersionFromSha(latestSha.get(),
                referenceDeployableVersion.getServiceId());

        if (deployableVersionFromSha == null) {
            assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Did not found deployable version matching the sha " + latestSha);
            throw new RuntimeException();
        } else {
            return deployableVersionFromSha;
        }
    }

    @POST("/deployable-version")
    public void addDeployableVersion(String gitCommitSha, String githubRepositoryUrl, int serviceId, Req req) {
        DeployableVersion newDeployableVersion = new DeployableVersion();

        // Getting the commit details
        String actualRepo = getRepoNameFromRepositoryUrl(githubRepositoryUrl);
        newDeployableVersion.setGitCommitSha(gitCommitSha);
        newDeployableVersion.setGithubRepositoryUrl(githubRepositoryUrl);
        newDeployableVersion.setServiceId(serviceId);

        // Just to protect tests from reaching github rate limit
        if (!githubRepositoryUrl.contains("test.com")) {

            // Not failing if this is null
            githubConnector.getCommitDetails(actualRepo, gitCommitSha).ifPresent(commitDetails -> {
                newDeployableVersion.setCommitUrl(commitDetails.getCommitUrl());
                newDeployableVersion.setCommitMessage(commitDetails.getCommitMessage());
                newDeployableVersion.setCommitDate(commitDetails.getCommitDate());
                newDeployableVersion.setCommitterAvatarUrl(commitDetails.getCommitterAvatarUrl());
                newDeployableVersion.setCommitterName(commitDetails.getCommitterName());
            });
        }

        deployableVersionDao.addDeployableVersion(newDeployableVersion);
        assignJsonResponseToReq(req, HttpStatus.CREATED, newDeployableVersion);
    }
}
