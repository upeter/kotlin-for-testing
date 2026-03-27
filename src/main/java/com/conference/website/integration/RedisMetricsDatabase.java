package com.conference.website.integration;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RedisMetricsDatabase {

    private final ConcurrentHashMap<Long, AtomicLong> viewCounterByTalk = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicLong> likeCounterByTalk = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicLong> attendCounterByTalk = new ConcurrentHashMap<>();

    public long incrementAndGetViews(long talkId) {
        return viewCounterByTalk.computeIfAbsent(talkId, ignored -> new AtomicLong()).incrementAndGet();
    }

    public long getViews(long talkId) {
        return viewCounterByTalk.getOrDefault(talkId, new AtomicLong()).get();
    }

    public long incrementAndGetLikes(long talkId) {
        return likeCounterByTalk.computeIfAbsent(talkId, ignored -> new AtomicLong()).incrementAndGet();
    }

    public long getLikes(long talkId) {
        return likeCounterByTalk.getOrDefault(talkId, new AtomicLong()).get();
    }

    public long incrementAndGetAttends(long talkId) {
        return attendCounterByTalk.computeIfAbsent(talkId, ignored -> new AtomicLong()).incrementAndGet();
    }

    public long getAttends(long talkId) {
        return attendCounterByTalk.getOrDefault(talkId, new AtomicLong()).get();
    }
}
