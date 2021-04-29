package com.g4.museo.events;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class LoginCalledEvent extends ApplicationEvent {
    public LoginCalledEvent(Stage stage){
        super(stage);
    }
    public Stage getStage(){
        return (Stage) getSource();
    }
}
