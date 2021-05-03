package com.g4.museo.ui.fxml;

import com.gluonhq.charm.glisten.control.TextField;
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
public class LoginFxmlController extends FXMLController implements Initializable {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    public void onReturn(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.close();
    }

    @FXML
    public void onLogin(ActionEvent event){
        /*UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(usernameField.getText(), passwordField.getText());
        LoginInitializer.onLogin(authReq);*/
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
