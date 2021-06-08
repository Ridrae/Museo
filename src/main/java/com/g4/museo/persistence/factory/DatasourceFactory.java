package com.g4.museo.persistence.factory;

import com.g4.museo.persistence.converter.*;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.g4.museo")
@EnableR2dbcRepositories
public class DatasourceFactory extends AbstractR2dbcConfiguration {
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
    }

    @Override
    protected List<Object> getCustomConverters() {
        List<Object> converterList = new ArrayList<>();
        converterList.add(new ArtworkBorrowWritingConverter());
        converterList.add(new ArtworkFullReadingConverter());
        converterList.add(new ArtworkWritingConverter());
        converterList.add(new CollectionReadingConverter());
        converterList.add(new CollectionWritingConverter());
        converterList.add(new StateReadingConverter());
        converterList.add(new OwnerReadingConverter());
        converterList.add(new ArtworkDetailsWritingConverter());
        return converterList;
    }
}
