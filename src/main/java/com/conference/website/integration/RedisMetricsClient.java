package com.conference.website.integration;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisMetricsClient implements MetricsClient {

    private static final Duration WRITE_LATENCY = Duration.ofMillis(40);
    private static final Duration READ_LATENCY = Duration.ofMillis(30);

    private final RedisMetricsDatabase metricsDatabase;

    public RedisMetricsClient(RedisMetricsDatabase metricsDatabase) {
        this.metricsDatabase = metricsDatabase;
    }

    @Override
    public Mono<Long> incrementViews(Long talkId) {
        return Mono.fromCallable(() -> metricsDatabase.incrementAndGetViews(talkId))
                .delayElement(WRITE_LATENCY);
    }

    @Override
    public Mono<Long> getViews(Long talkId) {
        return Mono.fromCallable(() -> metricsDatabase.getViews(talkId))
                .delayElement(READ_LATENCY);
    }
}
