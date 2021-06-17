package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class CollectionRefreshEvent extends ApplicationEvent {
    public CollectionRefreshEvent(Object source) {
        super(source);
    }
}
