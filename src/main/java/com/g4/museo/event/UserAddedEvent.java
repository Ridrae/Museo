package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class UserAddedEvent extends ApplicationEvent {
    public UserAddedEvent(Object o) {
        super(o);
    }
}
