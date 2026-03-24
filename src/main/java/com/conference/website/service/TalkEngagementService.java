package com.conference.website.service;

import com.conference.website.dto.TalkDto;
import com.conference.website.dto.TalkEngagementDto;
import com.conference.website.integration.BuzzClient;
import com.conference.website.integration.MetricsClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class TalkEngagementService {

    private static final Duration CLIENT_TIMEOUT = Duration.ofMillis(700);
    private static final double DEFAULT_BUZZ_SCORE = 0.35;

    private final TalkService talkService;
    private final MetricsClient metricsClient;
    private final BuzzClient buzzClient;

    public TalkEngagementService(TalkService talkService, MetricsClient metricsClient, BuzzClient buzzClient) {
        this.talkService = talkService;
        this.metricsClient = metricsClient;
        this.buzzClient = buzzClient;
    }

    public Mono<TalkEngagementDto> getEngagement(Long talkId) {
        Mono<TalkDto> talkMono = Mono.fromCallable(() -> talkService.getTalk(talkId))
                .subscribeOn(Schedulers.boundedElastic());

        Mono<Long> viewsMono = metricsClient.getViews(talkId)
                .timeout(CLIENT_TIMEOUT)
                .onErrorReturn(0L);

        Mono<Double> buzzMono = buzzClient.getBuzzScore(talkId)
                .timeout(CLIENT_TIMEOUT)
                .onErrorReturn(DEFAULT_BUZZ_SCORE);

        return Mono.zip(talkMono, viewsMono, buzzMono)
                .map(tuple -> toTalkEngagement(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private TalkEngagementDto toTalkEngagement(TalkDto talk, long views, double buzzScore) {
        double engagementScore = Math.round(((views * 0.2) + (buzzScore * 80.0)) * 100.0) / 100.0;
        return new TalkEngagementDto(talk.id(), talk.title(), views, buzzScore, engagementScore);
    }
}
