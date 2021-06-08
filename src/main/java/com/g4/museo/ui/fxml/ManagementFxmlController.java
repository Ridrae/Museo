package com.g4.museo.ui.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ManagementFxmlController extends FXMLController implements Initializable {

    @FXML
    public void onReturn(ActionEvent event){
        var scene = ((Node) event.getSource()).getScene();
        var stage = (Stage)scene.getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO complete management
    }
}
