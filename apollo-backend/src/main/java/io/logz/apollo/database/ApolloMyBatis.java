package io.logz.apollo.database;

import io.logz.apollo.configuration.ApolloConfiguration;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloMyBatis {

    private static ApolloMyBatis instance;
    private SqlSession session;
    private static final Logger logger = LoggerFactory.getLogger(ApolloMyBatis.class);

    private ApolloMyBatis(ApolloConfiguration configuration) {
        try {
            logger.info("Creating MyBatis instance");
            DataSource dataSource = new ApolloDataSource(configuration).getDataSource();
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("apollo", transactionFactory, dataSource);

            Configuration myBatisConfiguration = new Configuration(environment);
            myBatisConfiguration.addMappers("io.logz.apollo.dao");
            myBatisConfiguration.setMapUnderscoreToCamelCase(true);

            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(myBatisConfiguration);

            session = sqlSessionFactory.openSession();
        } catch (Exception e) {
            throw new RuntimeException("Could not create MyBatis instance!", e);
        }
    }

    // This is here because i dont want to pass the configuration every time
    public static void initialize(ApolloConfiguration configuration) {
        logger.info("Initializing MyBatis for the first time");
        instance = new ApolloMyBatis(configuration);
    }

    public static ApolloMyBatis getInstance() {
        if (instance == null) {
            throw new RuntimeException("You must first initialize ApolloMyBatis before getting an instance!");
        }

        return instance;
    }

    public static void close() {

        if (instance == null) {
            logger.info("Could not close MyBatis, as it was not initialized");

        } else if (instance.session == null) {
            logger.info("Could not close MyBatis, as the session is null");
        } else {
            logger.info("Closing MyBatis session");
            instance.session.close();
        }
    }

    public <T> T getDao(Class<T> clazz) {
        logger.debug("Returning DAO for {}", clazz.getName());
        return session.getMapper(clazz);
    }
}
