package io.logz.apollo;

import io.logz.apollo.auth.DeploymentPermission;
import io.logz.apollo.auth.PermissionsValidator;
import io.logz.apollo.auth.User;
import io.logz.apollo.clients.ApolloClient;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.kubernetes.KubernetesHandler;
import io.logz.apollo.kubernetes.KubernetesHandlerFactory;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import org.junit.Test;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by roiravhon on 1/26/17.
 */
public class playground {

    @Test
    public void tahat() {

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

        ApolloConfiguration apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();
        ApolloMyBatis.initialize(apolloConfiguration);

        try (ApolloMyBatis.ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);

            Environment environment = environmentDao.getEnvironment(2);
            Service service = serviceDao.getService(3);

            KubernetesHandler kubernetesHandler = KubernetesHandlerFactory.getOrCreateKubernetesHandler(environment);

            System.out.println(kubernetesHandler.getDeploymentLogs(environment, service));
        }

    }
}
