package com.g4.museo.ui.utils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class CachingManager {
    @Async
    @Scheduled(fixedRate = 5000)
    @CacheEvict(value = {"artworks", "returnDate", "collections", "owners"}, allEntries = true)
    public void cleanCache(){}
}
