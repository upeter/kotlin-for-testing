package com.conference.website.service;

import com.conference.website.data.builders.CreateSpeakerRequestBuilder;
import com.conference.website.data.builders.CreateTalkRequestBuilder;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.CreateSpeakerRequest;
import com.conference.website.dto.CreateTalkRequest;
import com.conference.website.dto.EngagementCountDto;
import com.conference.website.dto.EngagementUpdateRequest;
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
    void shouldRecordEngagementAndReadCurrentCountsWithStepVerifier() {
        //Arrange
        var createSpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().build();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest);
        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest().withPrimarySpeaker(savedSpeakerDto).build();
        var talk = talkService.createTalk(createTalkRequest);

        var firstRequest = new EngagementUpdateRequest(true, true, false);
        var secondRequest = new EngagementUpdateRequest(false, true, true);

        //Act & Assert
        StepVerifier.create(
                        Mono.zip(
                                        viewTrackingService.recordEngagement(talk.id(), firstRequest),
                                        viewTrackingService.recordEngagement(talk.id(), secondRequest)
                                )
                                .flatMap(recorded -> viewTrackingService.getCurrentEngagement(talk.id())
                                        .map(current -> List.of(recorded.getT1(), recorded.getT2(), current)))
                )
                .assertNext(result -> {
                    assertThat(result).hasSize(3);

                    EngagementCountDto current = result.get(2);

                    assertThat(current.views()).isEqualTo(1L);
                    assertThat(current.likes()).isEqualTo(2L);
                    assertThat(current.attends()).isEqualTo(1L);
                })
                .verifyComplete();
    }

}
