package com.g4.museo.ui.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class DisplayArtworkFxmlController extends FXMLController implements Initializable {

    @FXML
    public void onReturn(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
    }
}
