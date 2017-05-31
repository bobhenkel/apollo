package io.logz.apollo.di;

import io.logz.apollo.configuration.ApolloConfiguration;
import io.logz.apollo.database.DataSourceFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
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
        bind(DataSource.class).toInstance(dataSource);
    }

    private void init() {
        environmentId("production");

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

}
