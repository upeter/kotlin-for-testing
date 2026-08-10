package com.conference.website.integration

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Component
class RedisMetricsDatabase {

    private val viewCounterByTalk = ConcurrentHashMap<Long, AtomicLong>()
    private val likeCounterByTalk = ConcurrentHashMap<Long, AtomicLong>()
    private val attendCounterByTalk = ConcurrentHashMap<Long, AtomicLong>()

    fun incrementAndGetViews(talkId: Long): Long =
        viewCounterByTalk.computeIfAbsent(talkId) { AtomicLong() }.incrementAndGet()

    fun getViews(talkId: Long): Long =
        viewCounterByTalk.getOrDefault(talkId, AtomicLong()).get()

    fun incrementAndGetLikes(talkId: Long): Long =
        likeCounterByTalk.computeIfAbsent(talkId) { AtomicLong() }.incrementAndGet()

    fun getLikes(talkId: Long): Long =
        likeCounterByTalk.getOrDefault(talkId, AtomicLong()).get()

    fun incrementAndGetAttends(talkId: Long): Long =
        attendCounterByTalk.computeIfAbsent(talkId) { AtomicLong() }.incrementAndGet()

    fun getAttends(talkId: Long): Long =
        attendCounterByTalk.getOrDefault(talkId, AtomicLong()).get()
}
