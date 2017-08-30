package io.logz.apollo;

import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerStore;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by roiravhon on 1/26/17.
 */
public class playground {

    @Ignore
    @Test
    public void tahat() throws Exception {
        StandaloneApollo standaloneApollo = StandaloneApollo.getOrCreateServer();

//        DeploymentPermission deploymentPermission = new DeploymentPermission();
//        deploymentPermission.setId(1);
//        deploymentPermission.setServiceId(null);
//        deploymentPermission.setEnvironmentId(2);
//        deploymentPermission.setPermissionType(DeploymentPermission.PermissionType.ALLOW);
//
//        List<DeploymentPermission> deploymentPermissions = new LinkedList<>();
//        deploymentPermissions.add(deploymentPermission);
//
//        boolean a = PermissionsValidator.isAllowedToDeploy(2, 2, deploymentPermissions);
//        System.out.println(a);

//
//        List<Integer> tahatList = new LinkedList<>();
//        tahatList.add(5);
//        tahatList.add(4);
//        tahatList.add(3);
//        tahatList.add(2);
//        tahatList.add(1);
//
//        Optional<Integer> mavet = tahatList.stream().sorted()
//            .map(a -> a)
//        .reduce((c, d) -> d);
//
//        System.out.println(mavet.get());

        KubernetesHandlerStore kubernetesHandlerStore = standaloneApollo.getInstance(KubernetesHandlerStore.class);
        EnvironmentDao environmentDao = standaloneApollo.getInstance(EnvironmentDao.class);
        ServiceDao serviceDao = standaloneApollo.getInstance(ServiceDao.class);

        Environment environment = environmentDao.getEnvironment(2);
        Service service = serviceDao.getService(3);

        KubernetesHandler kubernetesHandler = kubernetesHandlerStore.getOrCreateKubernetesHandler(environment);

    }
}
