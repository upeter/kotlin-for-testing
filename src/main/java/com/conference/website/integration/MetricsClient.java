package com.conference.website.integration;

import reactor.core.publisher.Mono;

public interface MetricsClient {

    Mono<Long> incrementViews(Long talkId);

    Mono<Long> incrementLikes(Long talkId);

    Mono<Long> incrementAttends(Long talkId);

    Mono<Long> getViews(Long talkId);

    Mono<Long> getLikes(Long talkId);

    Mono<Long> getAttends(Long talkId);
}
