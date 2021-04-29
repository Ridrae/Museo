package com.g4.museo.listeners;

import com.g4.museo.events.ManagementCalledEvent;
import com.g4.museo.events.ReturnCalledEvent;
import com.g4.museo.ui.ManagementInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ManagementEventListener implements ApplicationListener<ManagementCalledEvent> {

    @Autowired
    ManagementInitializer managementInitializer;

    @Override
    public void onApplicationEvent(ManagementCalledEvent managementCalledEvent) {
        managementInitializer.managementEventHandler(managementCalledEvent.getStage());
    }
}
