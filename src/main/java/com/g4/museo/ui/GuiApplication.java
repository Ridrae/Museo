package com.g4.museo.ui;

import com.g4.museo.MuseoApplication;
import com.g4.museo.events.StageReadyEvent;
import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.jdbc.ArtworkJdbcDao;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class  GuiApplication extends Application {
    private ConfigurableApplicationContext applicationContext;
    private Stage stage;

    @Bean
    public Stage getStage(){
        return this.stage;
    }

    @Bean
    public ConfigurableApplicationContext getConfigurableApplicationContext(){
        return this.applicationContext;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        applicationContext.publishEvent(new StageReadyEvent(this.stage));
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(MuseoApplication.class).run();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
}
