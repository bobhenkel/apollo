package io.logz.apollo.auth;

import io.logz.apollo.dao.GroupPermissionDao;
import io.logz.apollo.dao.PermissionDao;
import io.logz.apollo.dao.UserGroupDao;
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

        List<Permission> userPermissions = getUserPermissions(userEmail);

        // First iterate over permissions with no environment or no service, as those are the weakest, giving "Deny" the latest word
        userPermissions.stream()
                .filter(permission -> {
                    if (permission.getEnvironmentId() == null && permission.getServiceId() == serviceId) {
                        return true;
                    } else if (permission.getServiceId() == null && permission.getEnvironmentId() == environmentId) {
                        return true;
                    }
                    return false;
                }).sorted(PermissionsValidator::sortPermissionsByAllowDeny)
                .forEachOrdered(permission -> {
                    if (permission.getPermissionType().equals(Permission.PermissionType.ALLOW)) {
                        gotPermission.set(true);
                    } else {
                        gotPermission.set(false);
                    }
        });

        // Now iterate over specific permissions, giving "Deny" the latest word
        userPermissions.stream()
                .filter(permission ->
                        Objects.equals(permission.getEnvironmentId(), environmentId) &&
                        Objects.equals(permission.getServiceId(), serviceId))
                .sorted(PermissionsValidator::sortPermissionsByAllowDeny)
                .forEachOrdered(permission -> {
                    if (permission.getPermissionType().equals(Permission.PermissionType.ALLOW)) {
                        gotPermission.set(true);
                    } else {
                        gotPermission.set(false);
                    }
                });

        return gotPermission.get();
    }

    private static List<Permission> getUserPermissions(String userEmail) {

        PermissionDao permissionDao = ApolloMyBatis.getDao(PermissionDao.class);
        GroupPermissionDao groupPermissionDao = ApolloMyBatis.getDao(GroupPermissionDao.class);
        UserGroupDao userGroupDao = ApolloMyBatis.getDao(UserGroupDao.class);

        return userGroupDao.getAllUserGroupsByUser(userEmail)
                .stream()
                .map(userGroup -> groupPermissionDao.getAllGroupPermissionsByGroup(userGroup.getGroupId()))
                .map(groupPermissions ->
                        groupPermissions.stream()
                        .map(groupPermission -> permissionDao.getPermission(groupPermission.getPermissionId()))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static int sortPermissionsByAllowDeny(Permission permission1, Permission permission2) {
        if (permission1.getPermissionType().equals(permission2.getPermissionType())) {
            return 0;
        } else if (permission1.getPermissionType().equals(Permission.PermissionType.ALLOW)) {
            return -1;
        } else {
            return 1;
        }
    }
}
