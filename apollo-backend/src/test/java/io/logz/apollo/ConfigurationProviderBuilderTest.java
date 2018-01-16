package io.logz.apollo;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.configuration.ApolloConfigurationProviderBuilder;
import io.logz.apollo.exceptions.ApolloClientException;
import io.logz.apollo.helpers.ApolloConsul;
import org.conf4j.core.ConfigurationProvider;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationProviderBuilderTest {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationProviderBuilderTest.class);
    private final static String CONFIGURATION_FILE_NAME = "testing_apollo.conf";
    private final static String CONFIGURATION_CONSUL_KEY = "test/apollo/testing_apollo.conf";
    private final static String DATABASE_ADDRESS_VALUE_AS_IN_RESOURCES_APOLLO_TESTING_CONF_FILE = "dummyHostname";

    private static String configurationFilePath;
    private static ApolloConsul standaloneConsul;
    private static ConfigurationProvider<ApolloConfiguration> configurationProvider;

    @BeforeClass
    static public void beforeClass() throws Exception {
        configurationFilePath = ConfigurationProviderBuilderTest.class.getResource("/" + CONFIGURATION_FILE_NAME).getFile();

        standaloneConsul = new ApolloConsul();
        standaloneConsul.start();
    }

    @AfterClass
    public static void afterClass() {
        try {
            standaloneConsul.stop();
        } catch (Exception e) {
            logger.info("Failed to stop standalone consul");
        }
    }

    @Before
    public void setupTest(){
        System.clearProperty("APOLLO_CONFIG_FILEPATH");
        System.clearProperty("APOLLO_CONSUL_URL");
        System.clearProperty("APOLLO_CONSUL_KEY");
    }

    @After
    public void teardownTest(){
        try {
            if (configurationProvider != null) configurationProvider.close();
        } catch (Exception e) {
            logger.info("Failed to stop standalone consul");
        }
    }

    @Test
    public void testExitIfNoConfigurationExist() throws ApolloClientException {
        assertThatThrownBy(() -> ApolloConfigurationProviderBuilder
                .build()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testFileConfiguration() throws ApolloClientException {
        System.setProperty("APOLLO_CONFIG_FILEPATH", configurationFilePath);

        configurationProvider = ApolloConfigurationProviderBuilder.build();

        assertThat(configurationProvider.get().getDatabase().getHost())
                .isEqualTo(DATABASE_ADDRESS_VALUE_AS_IN_RESOURCES_APOLLO_TESTING_CONF_FILE);
    }

    @Test
    public void testConsulConfiguration() throws ApolloClientException {
        System.setProperty("APOLLO_CONSUL_URL", standaloneConsul.getURL());
        System.setProperty("APOLLO_CONSUL_KEY", CONFIGURATION_CONSUL_KEY);

        String databaseAddressKey = "apollo.db.host";
        String databaseAddressValue = "consulDummyHostname";

        standaloneConsul.putValue(CONFIGURATION_CONSUL_KEY, databaseAddressKey, databaseAddressValue);

        configurationProvider = ApolloConfigurationProviderBuilder.build();

        assertThat(configurationProvider.get().getDatabase().getHost()).isEqualTo(databaseAddressValue);
    }

    @Test
    public void testFileAndConsulConfiguration() throws ApolloClientException {
        System.setProperty("APOLLO_CONFIG_FILEPATH", configurationFilePath);
        System.setProperty("APOLLO_CONSUL_URL", standaloneConsul.getURL());
        System.setProperty("APOLLO_CONSUL_KEY", CONFIGURATION_CONSUL_KEY);

        String databaseAddressKey = "apollo.db.host";
        String databaseAddressValue = "consulDummyHostname";

        standaloneConsul.putValue(CONFIGURATION_CONSUL_KEY, databaseAddressKey, databaseAddressValue);

        configurationProvider = ApolloConfigurationProviderBuilder.build();

        assertThat(configurationProvider.get().getDatabase().getHost()).isEqualTo(databaseAddressValue);
    }

}
