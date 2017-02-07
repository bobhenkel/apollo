package io.logz.apollo.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Sets;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.excpetions.ApolloParseException;
import io.logz.apollo.transformers.LabelsNormalizer;
import io.logz.apollo.transformers.deployment.BaseDeploymentTransformer;
import io.logz.apollo.transformers.deployment.DeploymentImageNameTransformer;
import io.logz.apollo.transformers.deployment.DeploymentLabelsTransformer;
import io.logz.apollo.transformers.service.BaseServiceTransformer;
import io.logz.apollo.transformers.service.ServiceLabelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by roiravhon on 1/31/17.
 */
public class ApolloToKubernetes {

    private static final Logger logger = LoggerFactory.getLogger(ApolloToKubernetes.class);
    private static final String APOLLO_UNIQUE_IDENTIFIER_KEY = "apollo_unique_identifier";
    private final io.logz.apollo.models.Deployment apolloDeployment;
    private final io.logz.apollo.models.Service apolloService;
    private final io.logz.apollo.models.Environment apolloEnvironment;
    private final io.logz.apollo.models.DeployableVersion apolloDeployableVersion;
    private final ServiceDao serviceDao;
    private final EnvironmentDao environmentDao;
    private final DeployableVersionDao deployableVersionDao;
    private final ObjectMapper mapper;

    private final Set<BaseDeploymentTransformer> deploymentTransformers;
    private final Set<BaseServiceTransformer> serviceTransformers;

    public ApolloToKubernetes(io.logz.apollo.models.Deployment apolloDeployment) {
        this.apolloDeployment = apolloDeployment;
        serviceDao = ApolloMyBatis.getDao(ServiceDao.class);
        environmentDao = ApolloMyBatis.getDao(EnvironmentDao.class);
        deployableVersionDao = ApolloMyBatis.getDao(DeployableVersionDao.class);

        mapper = new ObjectMapper(new YAMLFactory());

        apolloService = serviceDao.getService(apolloDeployment.getServiceId());
        apolloEnvironment = environmentDao.getEnvironment(apolloDeployment.getEnvironmentId());
        apolloDeployableVersion = deployableVersionDao.getDeployableVersion(apolloDeployment.getDeployableVersionId());

        // Define the set of transformers the deployment object will go through
        deploymentTransformers = Sets.newHashSet(Arrays.asList(
                new DeploymentImageNameTransformer(),
                new DeploymentLabelsTransformer()
        ));

        // Define the set of transformers the service object will go through
        serviceTransformers = Sets.newHashSet(Arrays.asList(
                new ServiceLabelTransformer()
        ));
    }

    public Deployment getKubernetesDeployment() throws ApolloParseException {
        try {
            // Convert the deployment object to fabric8 model
            Deployment deployment = mapper.readValue(apolloService.getDeploymentYaml(), Deployment.class);

            // Programmatically access to change all the stuff we need
            logger.debug("About to run {} transformations on the deployment yaml of deployment id {}",
                    deploymentTransformers.size(), apolloDeployment.getId());
            deploymentTransformers.forEach(transformer ->
                    transformer.transform(deployment, apolloDeployment, apolloService, apolloEnvironment, apolloDeployableVersion));

            return deployment;

        } catch (IOException e) {
            throw new ApolloParseException("Could not parse deployment YAML from DB", e);
        }
    }

    public Service getKubernetesService() throws ApolloParseException {
        try {
            // Convert the service object to fabric8 model
            Service service = mapper.readValue(apolloService.getServiceYaml(), Service.class);

            // Programmatically access to change all the stuff we need
            logger.debug("About to run {} transformations on the service yaml of deployment id {}",
                    deploymentTransformers.size(), apolloDeployment.getId());
            serviceTransformers.forEach(transformer ->
                    transformer.transform(service, apolloDeployment, apolloService, apolloEnvironment, apolloDeployableVersion));

            return service;

        } catch (IOException e) {
            throw new ApolloParseException("Could not parse deployment YAML from DB", e);
        }
    }

    String getApolloDeploymentUniqueIdentifierValue() {
        return getApolloDeploymentUniqueIdentifierValue(apolloEnvironment, apolloService);
    }

    public static String getApolloDeploymentUniqueIdentifierKey() {
        return APOLLO_UNIQUE_IDENTIFIER_KEY;
    }

    public static String getApolloDeploymentUniqueIdentifierValue(io.logz.apollo.models.Environment apolloEnvironment,
                                                                  io.logz.apollo.models.Service apolloService) {

        return getApolloUniqueIdentifierWithPrefix(apolloEnvironment, apolloService, "deployment");
    }

    public static String getApolloServiceUniqueIdentifier(io.logz.apollo.models.Environment apolloEnvironment,
                                                           io.logz.apollo.models.Service apolloService) {
        return getApolloUniqueIdentifierWithPrefix(apolloEnvironment, apolloService, "service");
    }

    private static String getApolloUniqueIdentifierWithPrefix(io.logz.apollo.models.Environment apolloEnvironment,
                                                              io.logz.apollo.models.Service apolloService,
                                                              String prefix) {
        return LabelsNormalizer.normalize("apollo_" + prefix + "_" + apolloEnvironment.getName() + "_" + apolloService.getName());
    }
}
