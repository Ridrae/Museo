package com.g4.museo.ui;

import com.g4.museo.events.StageReadyEvent;
import com.g4.museo.persistence.jdbc.ArtworkJdbcDao;
import com.g4.museo.ui.fxml.LoginFxmlController;
import com.g4.museo.ui.fxml.MainFxmlController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    @Autowired
    ArtworkJdbcDao artworkJdbcDao;


    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ui.fxml"));
            Parent root = loader.load();
            MainFxmlController mainFxmlController = (MainFxmlController) loader.getController();
            Scene mainScene= new Scene(root, 1280, 720);
            mainFxmlController.populateArtworkGrid(artworkJdbcDao.getAllArtwork(), artworkJdbcDao);
            stage.setScene(mainScene);
            stage.setTitle("Museo Application");
            //stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
