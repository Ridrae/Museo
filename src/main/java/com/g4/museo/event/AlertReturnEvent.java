package com.g4.museo.event;

import com.g4.museo.persistence.dto.ArtworkFull;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class AlertReturnEvent extends ApplicationEvent {
    public AlertReturnEvent(List<ArtworkFull> source) {
        super(source);
    }
}
