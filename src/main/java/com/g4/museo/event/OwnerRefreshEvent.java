package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class OwnerRefreshEvent extends ApplicationEvent {
    public OwnerRefreshEvent(Object o) {
        super(o);
    }
}
