package com.g4.museo.event;

import com.g4.museo.ui.fxml.FXMLController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

public class UserLoginEvent extends ApplicationEvent {
    protected Logger log = LoggerFactory.getLogger(UserLoginEvent.class);

    public UserLoginEvent(Object source) {
        super(source);
    }
}
