package com.conference.website.integration;

import reactor.core.publisher.Mono;

public interface MetricsClient {

    Mono<Long> incrementViews(Long talkId);

    Mono<Long> getViews(Long talkId);
}
