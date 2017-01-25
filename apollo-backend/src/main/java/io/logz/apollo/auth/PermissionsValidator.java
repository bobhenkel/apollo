package io.logz.apollo.auth;

import io.logz.apollo.dao.DeploymentGroupPermissionDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.DeploymentUserGroupDao;
import io.logz.apollo.database.ApolloMyBatis;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by roiravhon on 1/12/17.
 */
public class PermissionsValidator {

    public static boolean validatePermissions(int serviceId, int environmentId, String userEmail) {

        AtomicBoolean gotPermission = new AtomicBoolean(false);

        List<DeploymentPermission> userDeploymentPermissions = getUserPermissions(userEmail);

        // First iterate over permissions with no environment or no service, as those are the weakest, giving "Deny" the latest word
        userDeploymentPermissions.stream()
                .filter(permission -> {
                    if (permission.getEnvironmentId() == null && permission.getServiceId() == serviceId) {
                        return true;
                    } else if (permission.getServiceId() == null && permission.getEnvironmentId() == environmentId) {
                        return true;
                    }
                    return false;
                }).sorted(PermissionsValidator::sortPermissionsByAllowDeny)
                .forEachOrdered(permission -> {
                    if (permission.getPermissionType().equals(DeploymentPermission.PermissionType.ALLOW)) {
                        gotPermission.set(true);
                    } else {
                        gotPermission.set(false);
                    }
        });

        // Now iterate over specific permissions, giving "Deny" the latest word
        userDeploymentPermissions.stream()
                .filter(permission ->
                        Objects.equals(permission.getEnvironmentId(), environmentId) &&
                        Objects.equals(permission.getServiceId(), serviceId))
                .sorted(PermissionsValidator::sortPermissionsByAllowDeny)
                .forEachOrdered(permission -> {
                    if (permission.getPermissionType().equals(DeploymentPermission.PermissionType.ALLOW)) {
                        gotPermission.set(true);
                    } else {
                        gotPermission.set(false);
                    }
                });

        return gotPermission.get();
    }

    private static List<DeploymentPermission> getUserPermissions(String userEmail) {

        DeploymentPermissionDao deploymentPermissionDao = ApolloMyBatis.getDao(DeploymentPermissionDao.class);
        DeploymentGroupPermissionDao deploymentGroupPermissionDao = ApolloMyBatis.getDao(DeploymentGroupPermissionDao.class);
        DeploymentUserGroupDao deploymentUserGroupDao = ApolloMyBatis.getDao(DeploymentUserGroupDao.class);

        return deploymentUserGroupDao.getAllDeploymentUserGroupsByUser(userEmail)
                .stream()
                .map(userGroup -> deploymentGroupPermissionDao.getAllDeploymentGroupPermissionsByGroup(userGroup.getDeploymentGroupId()))
                .map(groupPermissions ->
                        groupPermissions.stream()
                        .map(groupPermission -> deploymentPermissionDao.getDeploymentPermission(groupPermission.getDeploymentPermissionId()))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static int sortPermissionsByAllowDeny(DeploymentPermission deploymentPermission1, DeploymentPermission deploymentPermission2) {
        if (deploymentPermission1.getPermissionType().equals(deploymentPermission2.getPermissionType())) {
            return 0;
        } else if (deploymentPermission1.getPermissionType().equals(DeploymentPermission.PermissionType.ALLOW)) {
            return -1;
        } else {
            return 1;
        }
    }
}
