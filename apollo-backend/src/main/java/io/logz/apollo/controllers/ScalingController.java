package io.logz.apollo.controllers;

import com.google.common.collect.ImmutableMap;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.GroupDao;
import io.logz.apollo.models.Group;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.PUT;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import javax.inject.Inject;
import java.util.Map;

import static io.logz.apollo.common.ControllerCommon.assignJsonResponseToReq;
import static java.util.Objects.requireNonNull;

@Controller
public class ScalingController {

    private final GroupDao groupDao;

    @Inject
    public ScalingController(GroupDao groupDao) {
        this.groupDao = requireNonNull(groupDao);
    }

    @LoggedIn
    @GET("/scaling/apollo-factor/{groupId}")
    public int getScalingFactor(int groupId) {
        return groupDao.getScalingFactor(groupId);
    }

    @LoggedIn
    @GET("/scaling/kubernetes-factor/{groupId}")
    public int getKubeScalingFactor(int groupId) {
        // TODO: implement
        return 0;
    }

    @LoggedIn
    @PUT("/scaling/{groupId}")
    public void updateScalingFactor(int groupId, int scalingFactor, Req req) {
        Group group = groupDao.getGroup(groupId);

        if (group == null) {
            Map<String, String> message = ImmutableMap.of("message", "Group not found");
            assignJsonResponseToReq(req, HttpStatus.NOT_FOUND, message);
            return;
        }

        group.setScalingFactor(scalingFactor);
        // TODO: change in kube too

        groupDao.updateScalingFactor(group);
        assignJsonResponseToReq(req, HttpStatus.OK, group);
    }
}
