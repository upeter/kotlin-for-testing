package com.conference.website.api;

import com.conference.website.data.builders.SpeakerBuilder;
import com.conference.website.data.builders.TalkBuilder;
import com.conference.website.data.builders.TalkGraphPersistence;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.TalkDto;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TagRepository;
import com.conference.website.repository.TalkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Transactional
class TalkControllerIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void shouldNotSeeUncommittedTalksViaRestTestClientButSeeThemAfterCommit() {
        Long speakerId = null;
        Long firstTalkId = null;
        Long secondTalkId = null;

        String uniqueSuffix = String.valueOf(System.nanoTime());
        String firstTitle = "RestTestClient transaction boundary - first - " + uniqueSuffix;
        String secondTitle = "RestTestClient transaction boundary - second - " + uniqueSuffix;

        try {
            Speaker primarySpeaker = SpeakerBuilder.aSpeaker()
                    .withName("Ada Lovelace " + uniqueSuffix)
                    .withEmail("ada." + uniqueSuffix + "@example.com")
                    .build();

            Talk firstTalk = TalkBuilder.aTalk()
                    .withTitle(firstTitle)
                    .withLevel(TalkLevel.INTERMEDIATE)
                    .withPrimarySpeaker(primarySpeaker)
                    .build();

            Talk secondTalk = TalkBuilder.aTalk()
                    .withTitle(secondTitle)
                    .withLevel(TalkLevel.ADVANCED)
                    .withPrimarySpeaker(primarySpeaker)
                    .build();

            List<Talk> savedTalks = TalkGraphPersistence.persistGraph(
                    List.of(firstTalk, secondTalk),
                    speakerRepository,
                    tagRepository,
                    talkRepository
            );

            speakerId = primarySpeaker.getId();
            firstTalkId = savedTalks.getFirst().getId();
            secondTalkId = savedTalks.getLast().getId();

            List<TalkDto> talksBeforeCommit = restTestClient.get()
                    .uri("/api/talks")
                    .exchangeSuccessfully()
                    .expectStatus().isOk()
                    .returnResult(new ParameterizedTypeReference<List<TalkDto>>() {
                    })
                    .getResponseBody();

            assertThat(talksBeforeCommit)
                    .extracting(TalkDto::title)
                    .doesNotContain(firstTitle, secondTitle);

            TestTransaction.flagForCommit();
            TestTransaction.end();

            List<TalkDto> talksAfterCommit = restTestClient.get()
                    .uri("/api/talks")
                    .exchangeSuccessfully()
                    .expectStatus().isOk()
                    .returnResult(new ParameterizedTypeReference<List<TalkDto>>() {
                    })
                    .getResponseBody();

            assertThat(talksAfterCommit)
                    .extracting(TalkDto::title)
                    .contains(firstTitle, secondTitle);
        }
        finally {
            if (firstTalkId != null && talkRepository.existsById(firstTalkId)) {
                talkRepository.deleteById(firstTalkId);
            }
            if (secondTalkId != null && talkRepository.existsById(secondTalkId)) {
                talkRepository.deleteById(secondTalkId);
            }
            if (speakerId != null && speakerRepository.existsById(speakerId)) {
                speakerRepository.deleteById(speakerId);
            }
        }
    }
}
