package com.g4.museo.events;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class ManagementCalledEvent extends ApplicationEvent {

    public ManagementCalledEvent(Stage stage) {
        super(stage);
    }

    public Stage getStage(){
        return (Stage) getSource();
    }
}
