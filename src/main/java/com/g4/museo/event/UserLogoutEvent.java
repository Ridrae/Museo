package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class UserLogoutEvent extends ApplicationEvent {
    public UserLogoutEvent(Object source) {
        super(source);
    }
}
