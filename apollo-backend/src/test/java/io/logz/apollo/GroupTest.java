package io.logz.apollo;

import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.models.Group;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static io.logz.apollo.helpers.ModelsGenerator.createAndSubmitGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GroupTest {

    private static ApolloTestClient apolloTestClient;

    @BeforeClass
    public static void initialize() {
        apolloTestClient = Common.signupAndLogin();
    }

    @Test
    public void testAddAndGetGroup() throws ApolloClientException {
        // Add group
        Group testGroup = createAndSubmitGroup(apolloTestClient);
        // Make sure we can't add it again
        assertThatThrownBy(() -> apolloTestClient.addGroup(testGroup)).isInstanceOf(ApolloClientException.class);

        // Get the group back from the api and validate the returned value
        Group returnedGroup = apolloTestClient.getGroupByName(testGroup.getName());

        assertThat(returnedGroup.getName()).isEqualTo(testGroup.getName());
        assertThat(returnedGroup.getServiceId()).isEqualTo(testGroup.getServiceId());
        assertThat(returnedGroup.getEnvironmentId()).isEqualTo(testGroup.getEnvironmentId());
        assertThat(returnedGroup.getScalingFactor()).isEqualTo(testGroup.getScalingFactor());
        assertThat(returnedGroup.getJsonParams()).isEqualTo(testGroup.getJsonParams());
    }

    @Test
    public void testGetAllGroups() throws ApolloClientException {
        // Add group
        Group testGroup = createAndSubmitGroup(apolloTestClient);

        // Get all groups, and filter for ours
        Optional<Group> groupFromApi = apolloTestClient.getAllGroups().stream()
                .filter(group -> group.getName().equals(testGroup.getName())).findFirst();

        boolean found = false;

        // Make sure we got the correct one
        if (groupFromApi.isPresent()) {
            if (groupFromApi.get().getName().equals(testGroup.getName()) &&
                    groupFromApi.get().getServiceId() == testGroup.getServiceId() &&
                    groupFromApi.get().getEnvironmentId() == testGroup.getEnvironmentId() &&
                    groupFromApi.get().getScalingFactor() == testGroup.getScalingFactor() &&
                    groupFromApi.get().getJsonParams().equals(testGroup.getJsonParams())) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void testGetAndUpdateScalingFactor() throws ApolloClientException {
        Group testGroup = createAndSubmitGroup(apolloTestClient);
        int groupId = apolloTestClient.getGroupByName(testGroup.getName()).getId();

        int newScalingFactor = 8;
        apolloTestClient.updateScalingFactor(groupId, newScalingFactor);
        assertThat(apolloTestClient.getScalingFactor(groupId)).isEqualTo(newScalingFactor);
    }
}