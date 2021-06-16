package com.g4.museo.persistence.factory;

import com.g4.museo.persistence.converter.*;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.g4.museo")
@EnableR2dbcRepositories
public class DatasourceFactory extends AbstractR2dbcConfiguration {
    /*@Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://museo-sql.mysql.database.azure.com/museo");
        dataSourceBuilder.username("dev");
        dataSourceBuilder.password("FVEgUUg4HNqR4bD8");
        return dataSourceBuilder.build();
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "mysql")
                .option(ConnectionFactoryOptions.HOST, "museo-sql.mysql.database.azure.com")
                .option(ConnectionFactoryOptions.PORT, 3306)
                .option(ConnectionFactoryOptions.DATABASE, "museo")
                .option(ConnectionFactoryOptions.USER, "dev")
                .option(ConnectionFactoryOptions.PASSWORD, "FVEgUUg4HNqR4bD8")
                .build();
        return ConnectionFactories.get(options);
    }*/

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://127.0.0.1:3306/museo");
        dataSourceBuilder.username("dev");
        dataSourceBuilder.password("FVEgUUg4HNqR4bD8");
        return dataSourceBuilder.build();
    }

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "mysql")
                .option(ConnectionFactoryOptions.HOST, "127.0.0.1")
                .option(ConnectionFactoryOptions.PORT, 3306)
                .option(ConnectionFactoryOptions.DATABASE, "museo")
                .option(ConnectionFactoryOptions.USER, "dev")
                .option(ConnectionFactoryOptions.PASSWORD, "FVEgUUg4HNqR4bD8")
                .build();
        return ConnectionFactories.get(options);
    }

    @Override
    protected List<Object> getCustomConverters() {
        List<Object> converterList = new ArrayList<>();
        converterList.add(new ArtworkFullReadingConverter());
        converterList.add(new ArtworkFullWritingConverter());
        converterList.add(new CollectionReadingConverter());
        converterList.add(new CollectionWritingConverter());
        converterList.add(new StateReadingConverter());
        converterList.add(new OwnerReadingConverter());
        return converterList;
    }
}
