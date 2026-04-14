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

import static com.conference.website.data.E08_ObjectMotherKt.createSpeakerRequest;
import static com.conference.website.data.E08_ObjectMotherKt.createTalkRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class E08_TalkServiceTest {

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
    void shouldCreateTalkAndSpeakerCorrectly() {
        //Arrange
        var createSpeakerRequest = new CreateSpeakerRequest(
                "Ada Lovelace",
                "ada@example.com",
                "Analytical Engines",
                "Pioneer in computing"
        );
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest);

        var createTalkRequest = new CreateTalkRequest(
                "Supercharging JVM tests",
                "Practical patterns to reduce noisy test code",
                TalkLevel.ADVANCED,
                60,
                savedSpeakerDto,
                List.of(),
                List.of()
        );
        var savedTalkDto = talkService.createTalk(createTalkRequest);
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);

        //Act
        var talks = talkService.listTalks();

        //Assert
        assertEquals(1, talks.size());
        assertEquals(savedTalkDto, expectedTalkDto);
        assertThat(savedTalkDto.ratings()).isEmpty();

        assertThat(expectedTalkDto.primarySpeaker())
                .extracting("name", "email", "company", "bio")
                .containsExactly(
                        "Ada Lovelace",
                        "ada@example.com",
                        "Analytical Engines",
                        "Pioneer in computing"
                );
    }











    @Test
    void shouldCreateTalkAndSpeakerCorrectly_UsingTestBuilders() {
        //Arrange
        var createSpeakerRequest = CreateSpeakerRequestBuilder
           .aCreateSpeakerRequest()
           .withName("Jack Vanilla")
           .withEmail("jva@example.com")
           .build();

        SpeakerDto savedSpeakerDto = speakerService
           .createSpeaker(createSpeakerRequest);

        //if withPrimarySpeaker is not invoked,
        // an error is thrown
        var createTalkRequest = CreateTalkRequestBuilder
                .aCreateTalkRequest()
                //.withPrimarySpeaker(savedSpeakerDto)
                .withDurationMinutes(20)
                .build();





        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);
        var expectedTalkDto = TestDtoConversions
           .toDto(savedTalkDto.id(), createTalkRequest);

        //Assert
        assertEquals(savedTalkDto, expectedTalkDto);
        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }













    @Test
    void shouldCreateTalkAndSpeakerCorrectly_UsingKotlinMethods() {
        //Arrange
        var speakerRequest = createSpeakerRequest(
           "Jack Vanilla",
           "jva@example.com")
           ;
        SpeakerDto savedSpeakerDto = speakerService
           .createSpeaker(speakerRequest);

        //if withPrimarySpeaker is not invoked,
        // an error is thrown
        var createTalkRequest = createTalkRequest(savedSpeakerDto);

        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Assert
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto, expectedTalkDto);

        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }



}
