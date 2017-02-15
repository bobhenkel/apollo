package io.logz.apollo.helpers;

import io.logz.apollo.auth.DeploymentGroup;
import io.logz.apollo.auth.PasswordManager;
import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.User;
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
        testEnvironment.setKubernetesNamespace("namespace-" + Common.randomStr(5));

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
        testService.setDeploymentYaml("");  // TODO: fill something real
        testService.setServiceYaml("");  // TODO: fill something real

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

    public static DeploymentGroup createDeploymentGroup() {
        DeploymentGroup testDeploymentGroup = new DeploymentGroup();
        testDeploymentGroup.setName("DeploymentGroup " + Common.randomStr(10));

        return testDeploymentGroup;
    }

    public static DeploymentPermission createAllowDeploymentPermission(Optional<Environment> relatedEnvironment, Optional<Service> relatedService) {
        return createDeploymentPermission(relatedEnvironment, relatedService, true);
    }

    public static DeploymentPermission createDenyDeploymentPermission(Optional<Environment> relatedEnvironment, Optional<Service> relatedService) {
        return createDeploymentPermission(relatedEnvironment, relatedService, false);
    }

    private static DeploymentPermission createDeploymentPermission(Optional<Environment> relatedEnvironment, Optional<Service> relatedService, boolean allow) {
        DeploymentPermission testDeploymentPermission = new DeploymentPermission();
        testDeploymentPermission.setName("DeploymentPermission " + Common.randomStr(10));
        relatedEnvironment.ifPresent(environment -> testDeploymentPermission.setEnvironmentId(environment.getId()));
        relatedService.ifPresent(service -> testDeploymentPermission.setServiceId(service.getId()));

        if (allow) {
            testDeploymentPermission.setPermissionType(DeploymentPermission.PermissionType.ALLOW);
        } else {
            testDeploymentPermission.setPermissionType(DeploymentPermission.PermissionType.DENY);
        }

        return testDeploymentPermission;
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
