package io.logz.apollo.transformers.deployment;

import com.google.common.collect.ImmutableMap;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.logz.apollo.kubernetes.ApolloToKubernetes;
import io.logz.apollo.transformers.LabelsNormalizer;

import java.util.Map;

/**
 * Created by roiravhon on 1/31/17.
 */
public class DeploymentLabelsTransformer implements BaseDeploymentTransformer {

    @Override
    public Deployment transform(Deployment deployment,
                                io.logz.apollo.models.Deployment apolloDeployment,
                                io.logz.apollo.models.Service apolloService,
                                io.logz.apollo.models.Environment apolloEnvironment,
                                io.logz.apollo.models.DeployableVersion apolloDeployableVersion) {

        Map<String, String> desiredLabels = ImmutableMap.<String, String> builder()
                .put("environment", LabelsNormalizer.normalize(apolloEnvironment.getName()))
                .put("geo_region", LabelsNormalizer.normalize(apolloEnvironment.getGeoRegion()))
                .put("service", LabelsNormalizer.normalize(apolloService.getName()))
                .put("current_commit_sha", LabelsNormalizer.normalize(apolloDeployableVersion.getGitCommitSha()))
                .put("availability", LabelsNormalizer.normalize(apolloEnvironment.getAvailability()))
                .put(ApolloToKubernetes.getApolloDeploymentUniqueIdentifierKey(), ApolloToKubernetes.getApolloDeploymentUniqueIdentifierValue(apolloEnvironment, apolloService))
                .build();

        Map<String, String> labelsFromDeployment = deployment.getSpec().getTemplate().getMetadata().getLabels();

        // Just make sure we are not overriding any label explicitly provided by the user
        desiredLabels.forEach((key, value) -> {
            if (!labelsFromDeployment.containsKey(key)) {
                labelsFromDeployment.put(key, value);
            }
        });

        // And add all back to the deployment
        deployment.getSpec().getTemplate().getMetadata().setLabels(labelsFromDeployment);

        return deployment;
    }
}
