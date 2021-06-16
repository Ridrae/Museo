package com.g4.museo.ui.utils;

import com.g4.museo.MuseoApplication;
import com.g4.museo.event.ArtworkRefreshedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CachingManager {
    protected Logger log = LoggerFactory.getLogger(CachingManager.class);

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Async
    @Scheduled(fixedDelay = 60000)
    @CacheEvict(value = {"collections", "owners", "states"}, allEntries = true)
    public void cleanCache(){
        log.info("Cache cleared");
        if(MuseoApplication.isApplicationReady()){
            log.info("Updating artwork list");
            applicationEventPublisher.publishEvent(new ArtworkRefreshedEvent(this));
        }

    }
}
