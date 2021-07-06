package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class UserRefreshEvent extends ApplicationEvent {
    public UserRefreshEvent(Object o) {
        super(o);
    }
}
