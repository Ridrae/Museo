package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class AppReadyEvent extends ApplicationEvent {
    public AppReadyEvent(Object source) {
        super(source);
    }
}
