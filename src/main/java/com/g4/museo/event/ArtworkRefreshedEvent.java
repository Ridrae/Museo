package com.g4.museo.event;

import org.springframework.context.ApplicationEvent;

public class ArtworkRefreshedEvent extends ApplicationEvent {

    public ArtworkRefreshedEvent(Object source) {
        super(source);
    }
}
