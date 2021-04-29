package com.g4.museo.listeners;

import com.g4.museo.events.ReturnCalledEvent;
import com.g4.museo.events.StageReadyEvent;
import com.g4.museo.ui.StageInitializer;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ReturnEventListener implements ApplicationListener<ReturnCalledEvent> {
    @Autowired
    StageInitializer stageInitializer;

    @Override
    public void onApplicationEvent(ReturnCalledEvent event) {
        Stage stage = event.getStage();
        stageInitializer.returnEventHandler(stage);
    }
}
