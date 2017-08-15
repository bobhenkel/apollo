package io.logz.apollo;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.models.Deployment;
import io.logz.apollo.models.Environment;
import io.logz.apollo.models.Service;
import io.logz.apollo.notifications.ApolloNotifications;
import org.apache.camel.test.AvailablePortFinder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ApolloNotificationsTest {

    private ApolloNotifications apolloNotifications;

    private static int slackMockPort = AvailablePortFinder.getNextAvailable();
    private static final String pseudoUrl = "/slack/submit";
    private static String slackMockDomain = "http://localhost:" + slackMockPort + pseudoUrl;

    @Rule
    public WireMockRule slackServiceMock = new WireMockRule(wireMockConfig().port(slackMockPort), false);

    @Before
    public void setUp() throws Exception {
        ServiceDao serviceDaoMock = mock(ServiceDao.class);
        Service testService = new Service();
        testService.setName("test-service");
        when(serviceDaoMock.getService(Mockito.anyInt())).thenReturn(testService);

        EnvironmentDao environmentDaoMock = mock(EnvironmentDao.class);
        Environment testEnvironment = new Environment();
        testEnvironment.setName("test-environment");
        when(environmentDaoMock.getEnvironment(Mockito.anyInt())).thenReturn(testEnvironment);

        ApolloConfiguration apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();
        apolloConfiguration.setSlackWebHookUrl(slackMockDomain);

        apolloNotifications = new ApolloNotifications(apolloConfiguration, serviceDaoMock, environmentDaoMock);
    }

    @Test
    public void test() throws ApolloClientException, ParseException, InterruptedException {
        mockSlackServer();

        Deployment deployment = createDummyDeployment();
        apolloNotifications.add(Deployment.DeploymentStatus.STARTED, deployment);

        waitForRequest();

        verify(postRequestedFor(urlEqualTo(pseudoUrl)));
    }

    private void mockSlackServer() {
        stubFor(post(urlEqualTo(pseudoUrl)).willReturn(WireMock.ok()));
    }

    private void waitForRequest() throws InterruptedException {
        long waitDuration = 1000;
        while (getAllServeEvents().size() == 0 && waitDuration-- > 0) {
            Thread.sleep(1);
        }
    }

    private Deployment createDummyDeployment() throws ParseException {
        Deployment deployment = new Deployment();
        deployment.setEnvironmentId(1);
        deployment.setDeployableVersionId(1);
        deployment.setId(1);
        deployment.setLastUpdate(new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ss").parse("2000-09-11T10:00:00"));
        deployment.setServiceId(1);
        deployment.setSourceVersion("version");
        deployment.setUserEmail("test@logz.io");
        return deployment;
    }
}
