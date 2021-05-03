package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class UserChangedEvent extends ApplicationEvent {

    public UserChangedEvent(Object source) {
        super(source);
    }
}
