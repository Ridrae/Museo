package com.g4.museo.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class ReturnCalledEvent extends ApplicationEvent {

    public ReturnCalledEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage(){
        return ((Stage) getSource());
    }
}
