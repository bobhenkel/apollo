package io.logz.apollo.transformers.service;

import com.google.common.collect.ImmutableMap;
import io.fabric8.kubernetes.api.model.Service;
import io.logz.apollo.kubernetes.ApolloToKubernetes;

import java.util.Map;

/**
 * Created by roiravhon on 1/31/17.
 */
public class ServiceLabelTransformer implements BaseServiceTransformer {
    @Override
    public Service transform(Service service,
                             io.logz.apollo.models.Deployment apolloDeployment,
                             io.logz.apollo.models.Service apolloService,
                             io.logz.apollo.models.Environment apolloEnvironment,
                             io.logz.apollo.models.DeployableVersion apolloDeployableVersion) {

        Map<String, String> desiredLabels = ImmutableMap.<String, String> builder()
                .put("environment", apolloEnvironment.getName())
                .put("geo_region", apolloEnvironment.getGeoRegion())
                .put("apollo_unique_identifier", ApolloToKubernetes.getApolloServiceUniqueIdentifier(apolloEnvironment, apolloService))
                .build();

        Map<String, String> labelsFromService = service.getMetadata().getLabels();

        // Just make sure we are not overriding any label explicitly provided by the user
        desiredLabels.forEach((key, value) -> {
            if (!labelsFromService.containsKey(key)) {
                labelsFromService.put(key, value);
            }
        });

        // And add all back to the deployment
        service.getMetadata().setLabels(labelsFromService);

        return service;
    }
}