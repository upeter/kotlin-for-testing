package com.conference.website.api;

import com.conference.website.data.builders.SpeakerBuilder;
import com.conference.website.data.builders.TalkBuilder;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.TalkDto;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TalkRepository;
import com.conference.website.utils.EntityLifecycleTestUtils;
import com.conference.website.utils.TransactionTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Transactional
@ActiveProfiles("it")
class TalkControllerAdvancedIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private SpeakerRepository speakerRepository;

    @Test
    void shouldUseHigherOrderUtilitiesToVerifyVisibilityBeforeAndAfterCommit() {
        String uniqueSuffix = String.valueOf(System.nanoTime());
        String firstTitle = "RestTestClient advanced boundary - first - " + uniqueSuffix;
        String secondTitle = "RestTestClient advanced boundary - second - " + uniqueSuffix;

        Speaker speaker = SpeakerBuilder.aSpeaker()
                .withName("Grace Hopper " + uniqueSuffix)
                .withEmail("grace." + uniqueSuffix + "@example.com")
                .build();

        EntityLifecycleTestUtils.doWithSpeaker(speakerRepository, speaker, savedSpeaker -> {
            Talk firstTalk = TalkBuilder.aTalk()
                    .withTitle(firstTitle)
                    .withLevel(TalkLevel.INTERMEDIATE)
                    .withPrimarySpeaker(savedSpeaker)
                    .build();

            EntityLifecycleTestUtils.doWithTalk(
                    talkRepository,
                    firstTalk,
                    savedFirstTalk -> {
                        Talk secondTalk = TalkBuilder.aTalk()
                                .withTitle(secondTitle)
                                .withLevel(TalkLevel.ADVANCED)
                                .withPrimarySpeaker(savedSpeaker)
                                .build();

                        EntityLifecycleTestUtils.doWithTalk(
                                talkRepository,
                                secondTalk,
                                savedSecondTalk -> {
                                    List<TalkDto> talksAfterCommit = TransactionTestUtils.withNewTransaction(() ->
                                            restTestClient.get()
                                            .uri("/api/talks")
                                            .header("X-Transaction-Timeout", "1000")
                                            .header("Authorization", "Bearer token")
                                            .exchangeSuccessfully()
                                            .expectStatus().isOk()
                                            .returnResult(new ParameterizedTypeReference<List<TalkDto>>() {
                                            })
                                            .getResponseBody());

                                    assertThat(talksAfterCommit)
                                            .extracting(TalkDto::title)
                                            .contains(firstTitle, secondTitle);
                                    return null;
                                }
                        );
                        return null;
                    }
            );
            return null;
        });
    }
}
