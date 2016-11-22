package io.logz.apollo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.logz.apollo.configuration.ApolloConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloDataSource {

    private static final Logger logger = LoggerFactory.getLogger(ApolloDataSource.class);

    private final String serverName;
    private final int port;
    private final String username;
    private final String password;
    private final String schemaName;

    public ApolloDataSource(ApolloConfiguration configuration) {
        this.serverName = configuration.getDbHost();
        this.port = configuration.getDbPort();
        this.username = configuration.getDbUser();
        this.password = configuration.getDbPassword();
        this.schemaName = configuration.getDbSchema();
    }

    public DataSource getDataSource() {

        Properties poolProperties = new Properties();
        poolProperties.setProperty("username", username);
        poolProperties.setProperty("password", password);
        poolProperties.setProperty("dataSourceClassName", com.mysql.jdbc.jdbc2.optional.MysqlDataSource.class.getName());
        poolProperties.setProperty("minimumIdle", String.valueOf(1));
        poolProperties.setProperty("maximumPoolSize", String.valueOf(50));
        poolProperties.setProperty("dataSource.serverName", serverName);
        poolProperties.setProperty("dataSource.port", String.valueOf(port));
        poolProperties.setProperty("dataSource.databaseName", schemaName);
        poolProperties.setProperty("dataSource.useUnicode", String.valueOf(true));
        poolProperties.setProperty("dataSource.characterEncoding", "UTF-8");
        poolProperties.setProperty("poolName", "apollo");
        poolProperties.setProperty("connectionTimeout", String.valueOf(5000));
        poolProperties.setProperty("registerMbeans", "true");

        logger.info("Creating connection pool with these parameters: {}", poolProperties.toString());
        HikariConfig hikariConfig = new HikariConfig(poolProperties);

        try {
            return new HikariDataSource(hikariConfig);
        } catch (Exception e) {
            throw new RuntimeException("Could not create connection pool, bailing out!", e);
        }
    }
}
