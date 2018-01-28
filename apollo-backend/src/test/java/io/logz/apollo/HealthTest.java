package io.logz.apollo;

import static org.assertj.core.api.Assertions.assertThat;
import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.helpers.StandaloneApollo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class HealthTest {

    private static ApolloTestClient apolloTestClient;
    private static StandaloneApollo standaloneApollo;

    @BeforeClass
    public static void initialize() throws ScriptException, IOException, SQLException, ApolloClientException {
        apolloTestClient = Common.signupAndLogin();
        standaloneApollo = StandaloneApollo.getOrCreateServer();
        standaloneApollo.startKubernetesHealth();
        ModelsGenerator.createAndSubmitEnvironment(apolloTestClient);
    }

    @AfterClass
    public static void stop() {
        standaloneApollo.stopKubernetesHealth();
    }

    @Test
    public void testHealthEndpoint() throws ApolloClientException {
        Map<Integer, Boolean> healthStatus = apolloTestClient.getHealth();
        assertThat(healthStatus.size()).isEqualTo(0);
    }

    @Test
    @Ignore
    public void testHealthEndpointWithUnhealthyEnvironment() throws ApolloClientException {
        // TODO: add unhealthy environment
        Common.waitABit(3);
        Map<Integer, Boolean> healthStatus = apolloTestClient.getHealth();
        assertThat(healthStatus.size()).isGreaterThan(0);
    }
}
