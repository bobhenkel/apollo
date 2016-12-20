package io.logz.apollo.controllers;

import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.models.DeployableVersion;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import java.util.List;

/**
 * Created by roiravhon on 12/20/16.
 */
@Controller
public class DeployableVersionController {

    private final DeployableVersionDao deployableVersionDao;

    public DeployableVersionController() {
        deployableVersionDao = ApolloMyBatis.getDao(DeployableVersionDao.class);
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
    @POST("/deployable-version")
    public void addDeployableVersion(String gitCommitSha, String githubRepositoryUrl, int relatedService, Req req) {

        DeployableVersion newDeployableVersion = new DeployableVersion();

        newDeployableVersion.setGitCommitSha(gitCommitSha);
        newDeployableVersion.setGithubRepositoryUrl(githubRepositoryUrl);
        newDeployableVersion.setRelatedService(relatedService);

        deployableVersionDao.addDeployableVersion(newDeployableVersion);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newDeployableVersion);
    }
}
