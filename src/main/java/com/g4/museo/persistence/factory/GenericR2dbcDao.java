package com.g4.museo.persistence.factory;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class GenericR2dbcDao {
    protected Logger log = LoggerFactory.getLogger(GenericR2dbcDao.class);

    @Qualifier("connectionFactory")
    @Autowired
    ConnectionFactory connectionFactory;

    public R2dbcEntityTemplate getR2dbcEntityTemplate(){
        R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);
        return  template;
    }

}
