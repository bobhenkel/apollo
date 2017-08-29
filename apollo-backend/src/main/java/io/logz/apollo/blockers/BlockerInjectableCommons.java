package io.logz.apollo.blockers;

import io.logz.apollo.dao.BlockerDefinitionDao;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.scm.GithubConnector;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Objects.requireNonNull;

/**
 * Created by roiravhon on 6/4/17.
 */
@Singleton
public class BlockerInjectableCommons {

    private final GithubConnector githubConnector;
    private final DeployableVersionDao deployableVersionDao;
    private final DeploymentDao deploymentDao;

    @Inject
    public BlockerInjectableCommons(GithubConnector githubConnector, DeployableVersionDao deployableVersionDao, DeploymentDao deploymentDao) {
        this.githubConnector = requireNonNull(githubConnector);
        this.deployableVersionDao = requireNonNull(deployableVersionDao);
        this.deploymentDao = requireNonNull(deploymentDao);
    }

    public GithubConnector getGithubConnector() {
        return githubConnector;
    }

    public DeployableVersionDao getDeployableVersionDao() { return deployableVersionDao; }

    public DeploymentDao getDeploymentDao() { return deploymentDao; }
}
