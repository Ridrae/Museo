package com.g4.museo.ui.fxml;

import com.g4.museo.event.UserLoginEvent;
import com.g4.museo.ui.utils.AlertWindowFactory;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import com.gluonhq.charm.glisten.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginFxmlController extends FXMLController implements Initializable {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    public void onReturn(ActionEvent event){
        var scene = ((Node) event.getSource()).getScene();
        var stage = (Stage)scene.getWindow();
        stage.close();
    }

    @FXML
    public void onLogin(ActionEvent event){
        String userName = usernameField.getText();
        String userPassword = passwordField.getText();
        try {
            Authentication request = new UsernamePasswordAuthenticationToken(userName, userPassword);
            Authentication result = authManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);
            AlertWindowFactory.create("Successful Login","Successfully logged in as " + SecurityContextHolder.getContext().getAuthentication().getName());
            applicationEventPublisher.publishEvent(new UserLoginEvent(this));
            ((Stage)this.getView().getScene().getWindow()).close();
        } catch (AuthenticationException | IOException e) {
            ErrorWindowFactory.create(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setDefaultButton(true);
    }
}
