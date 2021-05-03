package com.g4.museo.ui;

import com.g4.museo.events.ManagementCalledEvent;
import com.g4.museo.ui.fxml.MainFxmlController;
import com.g4.museo.ui.fxml.ManagementFxmlController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class ManagementInitializer {
    @Autowired
    ConfigurableApplicationContext applicationContext;

    static private ConfigurableApplicationContext appContext;

    @PostConstruct
    public void init(){
        appContext = applicationContext;
    }

    static public void onManagementEvent(Stage stage){
        appContext.publishEvent(new ManagementCalledEvent(stage));
    }

    public void managementEventHandler(Stage stage){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("management.fxml"));
            Parent root = loader.load();
            ManagementFxmlController managementFxmlController = (ManagementFxmlController) loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
