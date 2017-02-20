package io.logz.apollo.helpers;

import io.logz.apollo.auth.User;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;

/**
 * Created by roiravhon on 2/7/17.
 */
public class RealDeploymentGenerator {

    private final String DEFAULT_LABEL_KEY = "app";
    private final String DEFAULT_LABEL_VALUE = "nginx";

    private final Environment environment;
    private final Service service;
    private final DeployableVersion deployableVersion;
    private final User user;
    private final Deployment deployment;

    public RealDeploymentGenerator(String deploymentImageName, String extraLabelKey, String extraLabelValue) {

        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {

            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            EnvironmentDao environmentDao = apolloMyBatisSession.getDao(EnvironmentDao.class);
            ServiceDao serviceDao = apolloMyBatisSession.getDao(ServiceDao.class);
            DeployableVersionDao deployableVersionDao = apolloMyBatisSession.getDao(DeployableVersionDao.class);
            UserDao userDao = apolloMyBatisSession.getDao(UserDao.class);

            // Create all models in DB
            environment = ModelsGenerator.createEnvironment();
            environmentDao.addEnvironment(environment);

            service = ModelsGenerator.createService();
            service.setDeploymentYaml(getDeploymentKubernetesYaml(deploymentImageName, extraLabelKey, extraLabelValue));
            service.setServiceYaml(getServiceDeploymentYaml(extraLabelKey, extraLabelValue));
            serviceDao.addService(service);

            deployableVersion = ModelsGenerator.createDeployableVersion(service);
            deployableVersionDao.addDeployableVersion(deployableVersion);

            user = ModelsGenerator.createRegularUser();
            userDao.addUser(user);

            deployment = ModelsGenerator.createDeployment(service, environment, deployableVersion);
            deployment.setStatus(Deployment.DeploymentStatus.PENDING);
            deployment.setSourceVersion("abc" + Common.randomStr(5));
            deployment.setUserEmail(user.getUserEmail());
            deploymentDao.addDeployment(deployment);
        }
    }

    public String getDefaultLabelKey() {
        return DEFAULT_LABEL_KEY;
    }

    public String getDefaultLabelValue() {
        return DEFAULT_LABEL_VALUE;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Service getService() {
        return service;
    }

    public DeployableVersion getDeployableVersion() {
        return deployableVersion;
    }

    public User getUser() {
        return user;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void updateDeploymentStatus(Deployment.DeploymentStatus deploymentStatus) {
        try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
            DeploymentDao deploymentDao = apolloMyBatisSession.getDao(DeploymentDao.class);
            deploymentDao.updateDeploymentStatus(deployment.getId(), deploymentStatus);
        }
    }

    private String getDeploymentKubernetesYaml(String imageName, String extraLabelKey, String extraLabelValue) {

        return "apiVersion: extensions/v1beta1\n" +
                "kind: Deployment\n" +
                "metadata:\n" +
                "  labels:\n" +
                "    tahat: nginx\n" +
                "    " + DEFAULT_LABEL_KEY + ": " + DEFAULT_LABEL_VALUE + "\n" +
                "    " + extraLabelKey + ": " + extraLabelValue + "\n" +
                "  name: nginx\n" +
                "  namespace: default\n" +
                "spec:\n" +
                "  replicas: 1\n" +
                "  strategy:\n" +
                "    rollingUpdate:\n" +
                "      maxSurge: 1\n" +
                "      maxUnavailable: 0\n" +
                "    type: RollingUpdate\n" +
                "  template:\n" +
                "    metadata:\n" +
                "      labels:\n" +
                "        pod: label\n" +
                "    spec:\n" +
                "      containers:\n" +
                "      - image: " + imageName + "\n" +
                "        imagePullPolicy: Always\n" +
                "        name: roi-apollo-test\n" +
                "        ports:\n" +
                "        - containerPort: 80\n" +
                "          protocol: TCP\n" +
                "        resources: {}\n" +
                "      dnsPolicy: ClusterFirst\n" +
                "      restartPolicy: Always\n" +
                "      securityContext: {}\n" +
                "      terminationGracePeriodSeconds: 30";
    }

    private String getServiceDeploymentYaml(String extraLabelKey, String extraLabelValue) {
        return "apiVersion: v1\n" +
                "kind: Service\n" +
                "metadata:\n" +
                "  labels:\n" +
                "    " + DEFAULT_LABEL_KEY + ": " + DEFAULT_LABEL_VALUE + "\n" +
                "    " + extraLabelKey + ": " + extraLabelValue + "\n" +
                "  name: roi-test-service\n" +
                "  namespace: default\n" +
                "spec:  \n" +
                "  ports:\n" +
                "  - nodePort: 30002\n" +
                "    port: 80\n" +
                "    protocol: TCP\n" +
                "    targetPort: 80\n" +
                "  selector:\n" +
                "    app: nginx\n" +
                "  sessionAffinity: None\n" +
                "  type: NodePort\n" +
                "status:\n" +
                "  loadBalancer: {}";
    }
}
