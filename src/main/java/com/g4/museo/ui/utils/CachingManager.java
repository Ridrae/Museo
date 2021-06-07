package com.g4.museo.ui.utils;

import com.g4.museo.event.ArtworkRefreshedEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CachingManager {
    @Async
    @Scheduled(fixedRate = 5000)
    @CacheEvict(value = {"collections", "owners", "states"}, allEntries = true)
    public void cleanCache(){}
}
