package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class UserLoginEvent extends ApplicationEvent {

    public UserLoginEvent(Object source) {
        super(source);
    }
}
