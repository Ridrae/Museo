package com.g4.museo;

import com.g4.museo.ui.fxml.MainFxmlController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
@Configuration
public class MuseoApplication extends Application {

    Logger log = LoggerFactory.getLogger(MuseoApplication.class);

    private static String[] initialArgs;

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        log.info("Initializing Spring Context");
        super.init();
        applicationContext = SpringApplication.run(getClass(), initialArgs);
        initSecurity();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting JavaFx");
        Stage stage = primaryStage;
        stage.setTitle("Museo Application");
        stage.setResizable(false);
        MainFxmlController mainController = applicationContext.getBean(MainFxmlController.class);
        Scene mainScene = new Scene(mainController.getView());
        stage.setScene(mainScene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping");
        super.stop();
    }

    public static void main(String[] args) {
        initialArgs = args;
        launch(args);
    }

    public static void initSecurity() {
        SecurityContextHolder.setStrategyName("MODE_GLOBAL");
        initAnonymous();
    }

    public static void initAnonymous() {
        AnonymousAuthenticationToken auth = new AnonymousAuthenticationToken(
                "anonymous", "anonymous",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void logout(){
        SecurityContextHolder.clearContext();
        initAnonymous();
    }

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster =
                new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}
