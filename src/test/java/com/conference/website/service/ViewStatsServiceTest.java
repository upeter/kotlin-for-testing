package com.conference.website.service;

import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.CreateSpeakerRequest;
import com.conference.website.dto.CreateTalkRequest;
import com.conference.website.dto.SpeakerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ViewStatsServiceTest {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private TalkService talkService;

    @Autowired
    private ViewTrackingService viewTrackingService;

    @Test
    void shouldRecordViewAndReadCurrentViewsWithStepVerifier() {
        //Arrange
        String uniqueEmail = "ada-" + UUID.randomUUID() + "@example.com";
        CreateSpeakerRequest speakerRequest = new CreateSpeakerRequest(
                "Ada Lovelace",
                uniqueEmail,
                "Analytical Engines",
                "Pioneer in computing"
        );
        SpeakerDto speaker = speakerService.createSpeaker(speakerRequest);

        CreateTalkRequest talkRequest = new CreateTalkRequest(
                "Coroutines + Reactor",
                "Combining asynchronous sources",
                TalkLevel.ADVANCED,
                45,
                speaker,
                List.of(),
                List.of()
        );

        var talk = talkService.createTalk(talkRequest);

        //Act & Assert
        StepVerifier.create(
                        Mono.zip(
                                        viewTrackingService.recordView(talk.id()),
                                        viewTrackingService.recordView(talk.id())
                                )
                                .flatMap(recordedViews -> viewTrackingService.getCurrentViews(talk.id())
                                        .map(currentViews -> List.of(recordedViews.getT1(), recordedViews.getT2(), currentViews)))
                )
                .assertNext(result -> {
                    assertThat(result).hasSize(3);
                    assertThat(List.of(result.get(0), result.get(1))).containsExactlyInAnyOrder(1L, 2L);
                    assertThat(result.get(2)).isEqualTo(2L);
                })
                .verifyComplete();
    }

}
