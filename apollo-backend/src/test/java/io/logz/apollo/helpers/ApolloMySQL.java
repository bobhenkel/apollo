package io.logz.apollo.helpers;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import io.logz.apollo.configuration.ApolloConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.jdbc.ext.ScriptUtils;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by roiravhon on 11/23/16.
 */
public class ApolloMySQL {

    private static final Logger logger = LoggerFactory.getLogger(ApolloMySQL.class);
    private final MySQLContainer mysql;

    public ApolloMySQL() throws SQLException, IOException, ScriptException {

        // Create mysql instance
        logger.info("Starting MySQL container");
        mysql = new MySQLContainer("mysql:5.7");
        mysql.start();

        logger.info("Creating MySQL connection and creating the schema");
        Connection connection = mysql.createConnection("");
        ScriptUtils.executeSqlScript(connection, "/" , Files.toString(new File("apollo-schema.sql"), Charsets.UTF_8));
    }

    public String getContainerIpAddress() {
        return mysql.getContainerIpAddress();
    }

    public int getMappedPort() {
        return mysql.getMappedPort(3306);
    }

    public String getUsername() {
        return mysql.getUsername();
    }

    public String getPassword() {
        return mysql.getPassword();
    }

    public String getSchema() {
        // Its hard-coded into test containers, without a getter
        return "test";
    }
}
