package com.conference.website.service;

import com.conference.website.dto.EngagementCountDto;
import com.conference.website.dto.EngagementUpdateRequest;
import com.conference.website.repository.TalkRepository;
import com.conference.website.integration.MetricsClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
@Service
public class ViewTrackingService {

    private static final Duration CLIENT_TIMEOUT = Duration.ofSeconds(2);

    private final TalkRepository talkRepository;
    private final MetricsClient metricsClient;

    public ViewTrackingService(TalkRepository talkRepository, MetricsClient metricsClient) {
        this.talkRepository = talkRepository;
        this.metricsClient = metricsClient;
    }

    public Mono<EngagementCountDto> recordEngagement(long talkId, EngagementUpdateRequest request) {
        ensureTalkExists(talkId);

        Mono<Void> recordViews = request.view()
                ? metricsClient.incrementViews(talkId).then()
                : Mono.empty();
        Mono<Void> recordLikes = request.like()
                ? metricsClient.incrementLikes(talkId).then()
                : Mono.empty();
        Mono<Void> recordAttends = request.attend()
                ? metricsClient.incrementAttends(talkId).then()
                : Mono.empty();

        return Mono.when(recordViews, recordLikes, recordAttends)
                .timeout(CLIENT_TIMEOUT)
                .then(getCurrentEngagement(talkId));
    }

    public Mono<EngagementCountDto> getCurrentEngagement(long talkId) {
        ensureTalkExists(talkId);
        return Mono.zip(
                        metricsClient.getViews(talkId),
                        metricsClient.getLikes(talkId),
                        metricsClient.getAttends(talkId)
                )
                .timeout(CLIENT_TIMEOUT)
                .map(tuple -> new EngagementCountDto(talkId, tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    private void ensureTalkExists(long talkId) {
        if (!talkRepository.existsById(talkId)) {
            throw new NotFoundException("Talk not found: " + talkId);
        }
    }
}
