package io.logz.apollo.controllers;

import com.google.common.base.Splitter;
import io.logz.apollo.deployment.DeploymentHandler;
import io.logz.apollo.models.DeploymentGroupsResponseObject;
import io.logz.apollo.excpetions.ApolloDeploymentException;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Group;
import io.logz.apollo.dao.GroupDao;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;
import javax.inject.Inject;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;
import io.logz.apollo.common.HttpStatus;

import java.util.Optional;

@Controller
public class DeploymentGroupsController {

    private final DeploymentHandler deploymentHandler;
    private final GroupDao groupDao;
    private final static String GROUP_IDS_DELIMITER = ",";

    @Inject
    public DeploymentGroupsController(DeploymentHandler deploymentHandler, GroupDao groupDao) {
        this.deploymentHandler = requireNonNull(deploymentHandler);
        this.groupDao = requireNonNull(groupDao);
    }

    @LoggedIn
    @POST("/deployment-groups")
    public void addDeployment(int environmentId, int serviceId, int deployableVersionId, String groupIdsCsv, String deploymentMessage, Req req) throws NumberFormatException {

        DeploymentGroupsResponseObject responseObject = new DeploymentGroupsResponseObject();

        Iterable<String> groupIds = Splitter.on(GROUP_IDS_DELIMITER).omitEmptyStrings().trimResults().split(groupIdsCsv);

        for (String groupIdString : groupIds) {
            int groupId = Integer.parseInt(groupIdString);
            Group group = groupDao.getGroup(groupId);

            if (group == null) {
                responseObject.addUnsuccessfulGroup(groupId, "Non existing group.");
                continue;
            }

            if (group.getServiceId() != serviceId) {
                responseObject.addUnsuccessfulGroup(groupId,"The deployment service ID " + serviceId + " doesn't match the group service ID " + group.getServiceId());
                continue;
            }

            if (group.getEnvironmentId() != environmentId) {
                responseObject.addUnsuccessfulGroup(groupId, "The deployment environment ID " + environmentId + " doesn't match the group environment ID " + group.getEnvironmentId());
                continue;
            }

            try {
                Deployment deployment = deploymentHandler.addDeployment(environmentId, serviceId, deployableVersionId,
                        deploymentMessage, Optional.of(group), req);
                responseObject.addSuccessfulGroup(groupId, deployment);
            } catch (ApolloDeploymentException e) {
                responseObject.addUnsuccessfulGroup(groupId, e.getMessage());
            }
        }
        assignJsonResponseToReq(req, HttpStatus.CREATED, responseObject);
    }
}
