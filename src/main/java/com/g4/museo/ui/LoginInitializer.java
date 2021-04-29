package com.g4.museo.ui;

import com.g4.museo.events.LoginCalledEvent;
import com.g4.museo.events.ReturnCalledEvent;
import com.g4.museo.events.StageReadyEvent;
import com.g4.museo.ui.fxml.LoginFxmlController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class LoginInitializer implements ApplicationListener<LoginCalledEvent> {

    @Autowired
    ConfigurableApplicationContext applicationContext;

    static private ConfigurableApplicationContext appContext;

    @PostConstruct
    public void init(){
        appContext = applicationContext;
    }

    static public void onLoginEvent(Stage stage){
        appContext.publishEvent(new LoginCalledEvent(stage));
    }

    static public void onReturnEvent(Stage stage){
        appContext.publishEvent(new ReturnCalledEvent(stage));
    }

    @Override
    public void onApplicationEvent(LoginCalledEvent event) {
        try {
            Stage stage = event.getStage();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
            Parent root = loader.load();
            LoginFxmlController loginFxmlController = loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            //stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
