package com.conference.website.service;

import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.TalkDto;
import com.conference.website.dto.TalkEngagementDto;
import com.conference.website.integration.BuzzClient;
import com.conference.website.integration.MetricsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TalkEngagementServiceTest {

    @Mock
    private TalkService talkService;

    @Mock
    private MetricsClient metricsClient;

    @Mock
    private BuzzClient buzzClient;

    @Test
    void shouldComposeEngagementFromParallelMonos() {
        TalkEngagementService service = new TalkEngagementService(talkService, metricsClient, buzzClient);
        given(talkService.getTalk(7L)).willReturn(aTalk(7L, "Coroutines in Action"));
        given(metricsClient.getViews(7L)).willReturn(Mono.just(120L));
        given(buzzClient.getBuzzScore(7L)).willReturn(Mono.just(0.9));

        StepVerifier.create(service.getEngagement(7L))
                .assertNext(engagement -> assertThat(engagement)
                        .usingRecursiveComparison()
                        .isEqualTo(new TalkEngagementDto(7L, "Coroutines in Action", 120L, 0.9, 96.0)))
                .verifyComplete();
    }

    @Test
    void shouldFallbackToDefaultBuzzWhenBuzzClientErrors() {
        TalkEngagementService service = new TalkEngagementService(talkService, metricsClient, buzzClient);
        given(talkService.getTalk(13L)).willReturn(aTalk(13L, "Reactive Streams for Humans"));
        given(metricsClient.getViews(13L)).willReturn(Mono.just(10L));
        given(buzzClient.getBuzzScore(13L)).willReturn(Mono.error(new IllegalStateException("boom")));

        StepVerifier.create(service.getEngagement(13L))
                .assertNext(engagement -> {
                    assertThat(engagement.talkId()).isEqualTo(13L);
                    assertThat(engagement.buzzScore()).isEqualTo(0.35);
                    assertThat(engagement.engagementScore()).isEqualTo(30.0);
                })
                .verifyComplete();
    }

    private static TalkDto aTalk(Long id, String title) {
        return new TalkDto(
                id,
                title,
                "Abstract",
                TalkLevel.ADVANCED,
                45,
                null,
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                null
        );
    }
}
