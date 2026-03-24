package com.conference.website.service;

import com.conference.website.repository.TalkRepository;
import com.conference.website.integration.MetricsClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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

    public Mono<Long> recordView(long talkId) {
        ensureTalkExists(talkId);
        return metricsClient.incrementViews(talkId).timeout(CLIENT_TIMEOUT).map(Long::longValue);
    }

    public Mono<Long> getCurrentViews(long talkId) {
        ensureTalkExists(talkId);
        return metricsClient.getViews(talkId).timeout(CLIENT_TIMEOUT);
    }

    private void ensureTalkExists(long talkId) {
        if (!talkRepository.existsById(talkId)) {
            throw new NotFoundException("Talk not found: " + talkId);
        }
    }
}
