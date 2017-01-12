package io.logz.apollo.helpers;

import io.logz.apollo.auth.Group;
import io.logz.apollo.auth.GroupPermission;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.Permission;
import io.logz.apollo.auth.User;
import io.logz.apollo.auth.UserGroup;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;

import java.util.Optional;

/**
 * Created by roiravhon on 12/20/16.
 */
public class ModelsGenerator {

    public static Environment createEnvironment() {
        Environment testEnvironment = new Environment();
        testEnvironment.setName("env-name-" + Common.randomStr(5));
        testEnvironment.setGeoRegion("us-east-" + Common.randomStr(5));
        testEnvironment.setAvailability("PROD-" + Common.randomStr(5));
        testEnvironment.setKubernetesMaster("kube.prod." + Common.randomStr(5));
        testEnvironment.setKubernetesToken("AaBbCc" + Common.randomStr(10));

        return testEnvironment;
    }

    public static DeployableVersion createDeployableVersion(Service relatedService) {
        DeployableVersion testDeployableVersion = new DeployableVersion();
        testDeployableVersion.setGitCommitSha("abc129aed837f6" + Common.randomStr(5));
        testDeployableVersion.setGithubRepositoryUrl("http://github/logzio/" + Common.randomStr(5));
        testDeployableVersion.setServiceId(relatedService.getId());

        return testDeployableVersion;
    }

    public static Service createService() {
        Service testService = new Service();
        testService.setName("Prod app " + Common.randomStr(5));

        return testService;
    }

    public static Deployment createDeployment(Service relatedService, Environment relatedEnvironment,
                                              DeployableVersion relatedDeployableVersion, User relatedUser) {

        Deployment testDeployment = new Deployment();
        testDeployment.setEnvironmentId(relatedEnvironment.getId());
        testDeployment.setServiceId(relatedService.getId());
        testDeployment.setDeployableVersionId(relatedDeployableVersion.getId());
        testDeployment.setUserEmail(relatedUser.getUserEmail());
        testDeployment.setSourceVersion("abc1234" + Common.randomStr(10));

        return testDeployment;
    }

    public static Group createGroup() {
        Group testGroup = new Group();
        testGroup.setName("Group " + Common.randomStr(10));

        return testGroup;
    }

    public static UserGroup createUserGroup(User user, Group group) {
        UserGroup testGroup = new UserGroup();
        testGroup.setUserEmail(user.getUserEmail());
        testGroup.setGroupId(group.getId());

        return testGroup;
    }

    public static GroupPermission createGroupPermission(Group group, Permission permission) {
        GroupPermission testGroupPermission = new GroupPermission();
        testGroupPermission.setGroupId(group.getId());
        testGroupPermission.setPermissionId(permission.getId());

        return testGroupPermission;
    }

    public static Permission createAllowPermission(Optional<Environment> relatedEnvironment, Optional<Service> relatedService) {
        return createPermission(relatedEnvironment, relatedService, true);
    }

    public static Permission createDenyPermission(Optional<Environment> relatedEnvironment, Optional<Service> relatedService) {
        return createPermission(relatedEnvironment, relatedService, false);
    }

    private static Permission createPermission(Optional<Environment> relatedEnvironment, Optional<Service> relatedService, boolean allow) {
        Permission testPermission = new Permission();
        testPermission.setName("Permission " + Common.randomStr(10));
        relatedEnvironment.ifPresent(environment -> testPermission.setEnvironmentId(environment.getId()));
        relatedService.ifPresent(service -> testPermission.setServiceId(service.getId()));

        if (allow) {
            testPermission.setPermissionType(Permission.PermissionType.ALLOW);
        } else {
            testPermission.setPermissionType(Permission.PermissionType.DENY);
        }

        return testPermission;
    }

    public static User createRegularUser() {
        return createUser(false);
    }

    public static User createAdminUser() {
        return createUser(true);
    }

    private static User createUser(boolean admin) {

        User testUser = new User();
        testUser.setUserEmail("tahat+" + Common.randomStr(5) + "@logz.io");
        testUser.setFirstName("Tahat " + Common.randomStr(5));
        testUser.setLastName("Tahatson " + Common.randomStr(5));
        testUser.setHashedPassword(PasswordManager.encryptPassword(Common.DEFAULT_PASSWORD));
        testUser.setAdmin(admin);

        return testUser;
    }
}
