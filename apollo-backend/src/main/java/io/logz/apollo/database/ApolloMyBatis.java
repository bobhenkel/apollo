package io.logz.apollo.database;

import io.logz.apollo.configuration.ApolloConfiguration;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;

/**
 * Created by roiravhon on 11/20/16.
 */
public class ApolloMyBatis {

    private static ApolloMyBatis instance;
    private static SqlSessionFactory sqlSessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(ApolloMyBatis.class);

    public static class ApolloMyBatisSession implements Closeable {

        private SqlSession session;

        ApolloMyBatisSession(SqlSessionFactory sqlSessionFactory) {
            session = sqlSessionFactory.openSession(true);
        }

        @Override
        public void close() {
            session.close();
        }

        public <T> T getDao(Class<T> clazz) {
            return session.getMapper(clazz);
        }
    }

    private ApolloMyBatis(ApolloConfiguration configuration) {
        try {
            logger.info("Creating MyBatis instance");
            DataSource dataSource = DataSourceFactory.create(configuration.getDbHost(), configuration.getDbPort(),
                    configuration.getDbUser(), configuration.getDbPassword(), configuration.getDbSchema());
            migrateDatabase(dataSource);

            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("apollo", transactionFactory, dataSource);

            Configuration myBatisConfiguration = new Configuration(environment);
            myBatisConfiguration.addMappers("io.logz.apollo.dao");
            myBatisConfiguration.setMapUnderscoreToCamelCase(true);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(myBatisConfiguration);

        } catch (Exception e) {
            throw new RuntimeException("Could not create MyBatis instance!", e);
        }
    }

    // This is here because i dont want to pass the configuration every time
    public static void initialize(ApolloConfiguration configuration) {
        logger.info("Initializing MyBatis for the first time");
        instance = new ApolloMyBatis(configuration);
    }

    public static ApolloMyBatisSession getSession() {
        if (instance == null) {
            throw new RuntimeException("You must first initialize ApolloMyBatis before getting a Dao!");
        }

        return new ApolloMyBatisSession(sqlSessionFactory);
    }

    private void migrateDatabase(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

}
