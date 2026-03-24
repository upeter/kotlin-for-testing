package com.conference.website.integration;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class FakeBuzzClient implements BuzzClient {

    private static final Duration LATENCY = Duration.ofMillis(55);

    @Override
    public Mono<Double> getBuzzScore(Long talkId) {
        if (talkId % 13 == 0) {
            return Mono.delay(LATENCY)
                    .flatMap(ignored -> Mono.error(new IllegalStateException("Buzz stream unavailable")));
        }

        double score = ((talkId % 10) + 1) / 10.0;
        return Mono.just(score)
                .delayElement(LATENCY);
    }
}
