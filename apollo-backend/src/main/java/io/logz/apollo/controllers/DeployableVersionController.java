package io.logz.apollo.controllers;

import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.scm.CommitDetails;
import io.logz.apollo.scm.GithubConnector;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import java.util.List;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;

/**
 * Created by roiravhon on 12/20/16.
 */
@Controller
public class DeployableVersionController {

    @LoggedIn
    @GET("/deployable-version")
    public List<DeployableVersion> getAllDeployableVersion() {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeployableVersionDao deployableVersionDao = apolloMyBatisSession.getDao(DeployableVersionDao.class);
            return deployableVersionDao.getAllDeployableVersions();
        }
    }

    @LoggedIn
    @GET("/deployable-version/{id}")
    public DeployableVersion getDeployableVersion(int id) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeployableVersionDao deployableVersionDao = apolloMyBatisSession.getDao(DeployableVersionDao.class);
            return deployableVersionDao.getDeployableVersion(id);
        }
    }

    @LoggedIn
    @GET("/deployable-version/sha/{sha}")
    public DeployableVersion getDeployableVersionFromSha(String sha) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()){
            DeployableVersionDao deployableVersionDao = apolloMyBatisSession.getDao(DeployableVersionDao.class);
            return deployableVersionDao.getDeployableVersionFromSha(sha);
        }
    }

    @POST("/deployable-version")
    public void addDeployableVersion(String gitCommitSha, String githubRepositoryUrl, int serviceId, Req req) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeployableVersionDao deployableVersionDao = apolloMyBatisSession.getDao(DeployableVersionDao.class);
            DeployableVersion newDeployableVersion = new DeployableVersion();

            // Getting the commit details
            String actualRepo = githubRepositoryUrl.replaceFirst("https:\\/\\/github.com\\/", "");
            CommitDetails commitDetails = GithubConnector.getCommitDetails(actualRepo, gitCommitSha);

            newDeployableVersion.setGitCommitSha(gitCommitSha);
            newDeployableVersion.setGithubRepositoryUrl(githubRepositoryUrl);
            newDeployableVersion.setServiceId(serviceId);

            // Not failing if this is null
            if (commitDetails != null) {
                newDeployableVersion.setCommitUrl(commitDetails.getCommitUrl());
                newDeployableVersion.setCommitMessage(commitDetails.getCommitMessage());
                newDeployableVersion.setCommitDate(commitDetails.getCommitDate());
                newDeployableVersion.setCommitterAvatarUrl(commitDetails.getCommitterAvatarUrl());
                newDeployableVersion.setCommitterName(commitDetails.getCommitterName());
            }

            deployableVersionDao.addDeployableVersion(newDeployableVersion);
            assignJsonResponseToReq(req, HttpStatus.CREATED, newDeployableVersion);
        }
    }
}
