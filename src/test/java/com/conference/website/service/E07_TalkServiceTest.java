package com.conference.website.service;

import com.conference.website.data.builders.*;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.*;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TagRepository;
import com.conference.website.repository.TalkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.conference.website.data.ObjectMotherKt.createSpeakerRequest;
import static com.conference.website.data.ObjectMotherKt.createTalkRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class E07_TalkServiceTest {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private TalkService talkService;

    @Test
    void shouldCreateTalkAndSpeakerCorrectly_NoCopy() {
        //Arrange
        var primarySpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().withCompany("Tst AG").build();

        var coSpeakerRequest0 = new CreateSpeakerRequest(
                "Sec Undo",
                "sec.undo@example.com",
                primarySpeakerRequest.company(),
                primarySpeakerRequest.bio());

        //requires .from(...) methods for all builders
        var coSpeakerRequest = CreateSpeakerRequestBuilder.from(primarySpeakerRequest)
                .withName("Sec Undo")
                .withEmail("sec.undo@example.com").build();

        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(primarySpeakerRequest);
        SpeakerDto savedCoSpeakerDto = speakerService.createSpeaker(coSpeakerRequest);

        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest()
                .withPrimarySpeaker(savedSpeakerDto)
                .withCoSpeakers(List.of(savedCoSpeakerDto))
                .build();

        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Assert
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto.primarySpeaker(), expectedTalkDto.primarySpeaker());
        assertThat(savedTalkDto.coSpeakers())
                .hasSize(1)
                .containsExactly(savedCoSpeakerDto);
        assertThat(List.of(savedTalkDto.primarySpeaker().company(), savedCoSpeakerDto.company())).contains("Tst AG");
    }
}
