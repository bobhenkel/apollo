package io.logz.apollo.configuration;

import com.google.common.annotations.VisibleForTesting;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloConfiguration {

    private int dbPort;
    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private String dbSchema;
    private String apiListen;
    private int apiPort;
    private int monitorThreadFrequencySeconds;
    private String githubLogin;
    private String githubOauthToken;
    private String secret;
    private int wsPort;
    private int wsIdleTimeoutSeconds;
    private String slackWebHookUrl;
    private String slackChanel;

    @VisibleForTesting
    public ApolloConfiguration(int dbPort, String dbHost, String dbUser, String dbPassword,
                               String dbSchema, String apiListen, int apiPort,
                               int monitorThreadFrequencySeconds, String githubLogin, String githubOauthToken,
                               String secret, int wsPort, int wsIdleTimeoutSeconds, String slackWebHookUrl, String slackChanel) {
        this.dbPort = dbPort;
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbSchema = dbSchema;
        this.apiListen = apiListen;
        this.apiPort = apiPort;
        this.monitorThreadFrequencySeconds = monitorThreadFrequencySeconds;
        this.githubLogin = githubLogin;
        this.githubOauthToken = githubOauthToken;
        this.secret = secret;
        this.wsPort = wsPort;
        this.wsIdleTimeoutSeconds = wsIdleTimeoutSeconds;
        this.slackWebHookUrl = slackWebHookUrl;
        this.slackChanel = slackChanel;
    }

    public static ApolloConfiguration parseConfigurationFromResources() {

        Config config = ConfigFactory.load();

        // Database related
        int dbPort = config.getInt("apollo.db.port");
        String dbHost = config.getString("apollo.db.host");
        String dbUser = config.getString("apollo.db.user");
        String dbPassword = config.getString("apollo.db.password");
        String dbSchema = config.getString("apollo.db.schema");

        // API Related
        String apiListen = config.getString("apollo.api.listen");
        int apiPort = config.getInt("apollo.api.port");
        String secret = config.getString("apollo.api.secret");

        // Kubernetes related
        int monitorThreadFrequencySeconds = config.getInt("apollo.kubernetes.monitoringFrequencySeconds");

        // SCM related
        String githubLogin = config.getString("apollo.scm.githubLogin");
        String githubOauthToken = config.getString("apollo.scm.githubOauthToken");

        // Websocket related
        int wsPort = config.getInt("apollo.websocket.port");
        int wsIdleTimeoutSeconds = config.getInt("apollo.websocket.idleTimeoutSeconds");

        // Notifications related
        String slackWebHookUrl = config.getString("apollo.notifications.slack.webhookUrl");
        String slackChanel = config.getString("apollo.notifications.slack.channel");

        return new ApolloConfiguration(dbPort, dbHost, dbUser, dbPassword, dbSchema, apiListen, apiPort,
                monitorThreadFrequencySeconds, githubLogin, githubOauthToken, secret, wsPort, wsIdleTimeoutSeconds,
                                       slackWebHookUrl, slackChanel);
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbSchema() {
        return dbSchema;
    }

    public String getApiListen() {
        return apiListen;
    }

    public int getApiPort() {
        return apiPort;
    }

    public int getMonitorThreadFrequencySeconds() {
        return monitorThreadFrequencySeconds;
    }

    public String getGithubLogin() {
        return githubLogin;
    }

    public String getGithubOauthToken() {
        return githubOauthToken;
    }

    public String getSecret() {
        return secret;
    }

    public int getWsPort() {
        return wsPort;
    }

    public int getWsIdleTimeoutSeconds() {
        return wsIdleTimeoutSeconds;
    }

    public String getSlackWebHookUrl() {
        return slackWebHookUrl;
    }

    public String getSlackChanel() {
        return slackChanel;
    }

    @VisibleForTesting
    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    @VisibleForTesting
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    @VisibleForTesting
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    @VisibleForTesting
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    @VisibleForTesting
    public void setDbSchema(String dbSchema) {
        this.dbSchema = dbSchema;
    }

    @VisibleForTesting
    public void setApiListen(String apiListen) {
        this.apiListen = apiListen;
    }

    @VisibleForTesting
    public void setApiPort(int apiPort) {
        this.apiPort = apiPort;
    }

    @VisibleForTesting
    public void setMonitorThreadFrequencySeconds(int monitorThreadFrequencySeconds) {
        this.monitorThreadFrequencySeconds = monitorThreadFrequencySeconds;
    }

    @VisibleForTesting
    public void setGithubLogin(String githubLogin) {
        this.githubLogin = githubLogin;
    }

    @VisibleForTesting
    public void setGithubOauthToken(String githubOauthToken) {
        this.githubOauthToken = githubOauthToken;
    }

    @VisibleForTesting
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @VisibleForTesting
    public void setWsPort(int wsPort) {
        this.wsPort = wsPort;
    }

    @VisibleForTesting
    public void setWsIdleTimeoutSeconds(int wsIdleTimeoutSeconds) {
        this.wsIdleTimeoutSeconds = wsIdleTimeoutSeconds;
    }

    @VisibleForTesting
    public void setSlackWebHookUrl(String slackWebHookUrl) {
        this.slackWebHookUrl = slackWebHookUrl;
    }

    @VisibleForTesting
    public void setSlackChanel(String slackChanel) {
        this.slackChanel = slackChanel;
    }
}
