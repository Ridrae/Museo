package com.g4.museo.persistence.factory;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.g4.museo")
public class DatasourceFactory {
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://museo-sql.mysql.database.azure.com/museo");
        dataSourceBuilder.username("dev");
        dataSourceBuilder.password("FVEgUUg4HNqR4bD8");
        return dataSourceBuilder.build();
    }
}
