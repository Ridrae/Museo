package com.g4.museo.ui.fxml;

import com.g4.museo.ui.LoginInitializer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFxmlController {
    @FXML
    public void onReturn(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        LoginInitializer.onReturnEvent(stage);
    }
}
