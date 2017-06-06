package io.logz.apollo.di;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.dao.BlockerDefinitionDao;
import io.logz.apollo.dao.DeployableVersionDao;
import io.logz.apollo.dao.DeploymentDao;
import io.logz.apollo.dao.DeploymentGroupDao;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.dao.UserDao;
import io.logz.apollo.database.DataSourceFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.flywaydb.core.Flyway;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.configuration.ConfigurationProvider;
import org.mybatis.guice.environment.EnvironmentProvider;
import org.mybatis.guice.session.SqlSessionFactoryProvider;
import org.mybatis.guice.session.SqlSessionManagerProvider;

import javax.sql.DataSource;

public class ApolloMyBatisModule extends MyBatisModule {

    private final DataSource dataSource;

    public ApolloMyBatisModule(ApolloConfiguration configuration) {
        this.dataSource = createDatasource(configuration);
    }

    @Override
    protected void initialize() {
        init();
        migrateDatabase(dataSource);

        addMapperClass(DeployableVersionDao.class);
        addMapperClass(DeploymentDao.class);
        addMapperClass(DeploymentGroupDao.class);
        addMapperClass(DeploymentPermissionDao.class);
        addMapperClass(EnvironmentDao.class);
        addMapperClass(ServiceDao.class);
        addMapperClass(UserDao.class);
        addMapperClass(BlockerDefinitionDao.class);
    }

    private void init() {
        environmentId("production");

        bind(DataSource.class).toInstance(dataSource);
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        bind(SqlSessionManagerProvider.class);
        bind(EnvironmentProvider.class);
        bind(ConfigurationProvider.class);
        bind(SqlSessionFactoryProvider.class);

        mapUnderscoreToCamelCase(true);
    }

    private DataSource createDatasource(ApolloConfiguration configuration) {
        String host = configuration.getDbHost();
        int port = configuration.getDbPort();
        String user = configuration.getDbUser();
        String password = configuration.getDbPassword();
        String schema = configuration.getDbSchema();
        return DataSourceFactory.create(host, port, user, password, schema);
    }

    private void migrateDatabase(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

}
