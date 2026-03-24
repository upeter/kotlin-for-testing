package com.conference.website.integration;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RedisMetricsDatabase {

    private final ConcurrentHashMap<Long, AtomicLong> viewCounterByTalk = new ConcurrentHashMap<>();

    public long incrementAndGetViews(long talkId) {
        return viewCounterByTalk.computeIfAbsent(talkId, ignored -> new AtomicLong()).incrementAndGet();
    }

    public long getViews(long talkId) {
        return viewCounterByTalk.getOrDefault(talkId, new AtomicLong()).get();
    }
}
