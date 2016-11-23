package io.logz.apollo.helpers;

import io.logz.apollo.configuration.ApolloConfiguration;

import javax.script.ScriptException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by roiravhon on 11/23/16.
 */
public class ApolloHelper {

    private static ApolloHelper instance;

    private ApolloConfiguration apolloConfiguration;
    private MySqlHelper mySqlHelper;
    private RestClientHelper restClientHelper;

    private ApolloHelper() throws ScriptException, SQLException, IOException {

        apolloConfiguration = ApolloConfiguration.parseConfigurationFromResources();

        mySqlHelper = new MySqlHelper(apolloConfiguration);
        restClientHelper = new RestClientHelper(apolloConfiguration);
    }

    public static ApolloHelper getInstance() throws ScriptException, IOException, SQLException {

        if (instance == null) {
            instance = new ApolloHelper();
        }

        return instance;
    }

    public RestClientHelper rest() {
        return restClientHelper;
    }

    public MySqlHelper mysql() {
        return mySqlHelper;
    }
}
