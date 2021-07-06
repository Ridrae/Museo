package com.g4.museo.event;

import com.g4.museo.ui.fxml.ManagementFxmlController;
import org.springframework.context.ApplicationEvent;

public class StateRefreshEvent extends ApplicationEvent {
    public StateRefreshEvent(Object source) {
        super(source);
    }
}
