package io.logz.apollo;

import io.logz.apollo.clients.ApolloTestClient;
import io.logz.apollo.helpers.Common;
import io.logz.apollo.helpers.ModelsGenerator;
import io.logz.apollo.helpers.StandaloneApollo;
import io.logz.apollo.models.DeployableVersion;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.notifications.ApolloNotifications;
import io.logz.apollo.models.Notification.NotificationType;
import io.logz.apollo.models.Notification;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

public class NotificationsTest {

    private static int slackMockPort = Common.getAvailablePort();
    private static final String pseudoUrl = "/slack/submit";
    private static String slackMockDomain = "http://localhost:" + slackMockPort + pseudoUrl;
    private static String slackNotificationConfiguration;

    private MockServerClient mockServerClient;

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this, slackMockPort);

    @BeforeClass
    public static void beforeClass() {
        slackNotificationConfiguration = "{\n" +
                "        \"channel\": \"#slack\",\n" +
                "        \"webhookUrl\": \""+ slackMockDomain +"\"\n" +
                "}";
    }

    @Test
    public void testSlackNotification() throws Exception {

        ApolloTestClient apolloTestClient = Common.signupAndLogin();
        Notification notification = ModelsGenerator.createAndSubmitNotification(apolloTestClient,
                NotificationType.SLACK, slackNotificationConfiguration);

        Environment environment = apolloTestClient.getEnvironment(notification.getEnvironmentId());
        Service service = apolloTestClient.getService(notification.getServiceId());

        DeployableVersion deployableVersion = ModelsGenerator.createAndSubmitDeployableVersion(apolloTestClient, service);
        Deployment deployment = ModelsGenerator.createAndSubmitDeployment(apolloTestClient, environment, service, deployableVersion);

        mockSlackServer();

        StandaloneApollo.getOrCreateServer().getInstance(ApolloNotifications.class).notify(Deployment.DeploymentStatus.DONE, deployment);

        waitForRequest();

        mockServerClient.verify(
                HttpRequest.request()
                .withMethod("POST")
                .withPath(pseudoUrl),
                VerificationTimes.exactly(1)
        );
    }

    private void mockSlackServer() {

        mockServerClient.when(
                HttpRequest.request()
                .withMethod("POST")
                .withPath(pseudoUrl)
        ).respond(
                HttpResponse.response()
                .withStatusCode(200)
        );
    }

    private void waitForRequest() throws InterruptedException {
        long waitDuration = 100;
        while (mockServerClient.retrieveRecordedRequests(HttpRequest.request()
                .withMethod("POST")
                .withPath(pseudoUrl)
        ).length == 0 && waitDuration-- > 0) {
            Thread.sleep(10);
        }
    }
}
