package com.conference.website.integration;

import reactor.core.publisher.Mono;

public interface BuzzClient {

    Mono<Double> getBuzzScore(Long talkId);
}
