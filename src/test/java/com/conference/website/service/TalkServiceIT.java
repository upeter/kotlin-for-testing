package com.conference.website.service;

import com.conference.website.data.ObjectMotherKt;
import com.conference.website.data.builders.CreateSpeakerRequestBuilder;
import com.conference.website.data.builders.CreateTalkRequestBuilder;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.CreateSpeakerRequest;
import com.conference.website.dto.CreateTalkRequest;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.dto.TestDtoConversions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.conference.website.data.ObjectMotherKt.createSpeakerRequest;
import static com.conference.website.data.ObjectMotherKt.createTalkRequest;
import static com.conference.website.dto.DtoConversions.toDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TalkServiceIT {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private TagService tagService;

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
                .extracting("id", "name", "email", "company", "bio")
                .containsExactly(
                        expectedTalkDto.id(),
                        "Ada Lovelace",
                        "ada@example.com",
                        "Analytical Engines",
                        "Pioneer in computing"
                );



//        assertThat(savedSpeakerDto.name()).isEqualTo("Ada Lovelace");
//        assertThat(savedSpeakerDto.email()).isEqualTo("ada@example.com");
//        assertThat(savedSpeakerDto.company()).isEqualTo("Analytical Engines");
//        assertThat(savedSpeakerDto.bio()).isEqualTo("Pioneer in computing");
//
//
//        assertThat(savedSpeakerDto)
//                .extracting("id", "name", "email", "company", "bio")
//                .containsExactly(
//                        savedSpeakerDto.id(),
//                        "Ada Lovelace",
//                        "ada@example.com",
//                        "Analytical Engines",
//                        "Pioneer in computing"
//                );


//        assertThat(savedSpeakerDto).isNotNull().satisfies(saved -> {
//                    assertThat(saved.name()).isEqualTo(expectedSpeakerDto.name());
//                    assertThat(saved.email()).isEqualTo(expectedSpeakerDto.email());
//                    assertThat(saved.company()).isEqualTo(expectedSpeakerDto.company());
//                    assertThat(saved.bio()).isEqualTo(expectedSpeakerDto.bio());
//                });
//
//        assertThat(savedSpeakerDto).isEqualTo(expectedSpeakerDto);

//        var scheduleSlotDto = talkService.assignSchedule(savedTalkDto.id(), new ScheduleSlotRequest(
//                "Room B",
//                LocalDateTime.of(2026, 4, 8, 14, 0),
//                LocalDateTime.of(2026, 4, 8, 15, 0)
//        ));


    }

    @Test
    void shouldCreateTalkAndSpeakerCorrectly_UsingTestBuilders() {
        //Arrange
        var createSpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().build();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest);

        //show that if withPrimarySpeaker is not invoked an error is thrown
        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest().withPrimarySpeaker(savedSpeakerDto).build();
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Act
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);

        //Assert
        assertEquals(savedTalkDto, expectedTalkDto);

        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }


    @Test
    void shouldCreateTalkAndSpeakerCorrectly_UsingKotlinMethods() {
        //Arrange
        var speakerRequest = createSpeakerRequest();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(speakerRequest);

        //show that if withPrimarySpeaker is not invoked an error is thrown
        var createTalkRequest = createTalkRequest(savedSpeakerDto);

        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Assert
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto, expectedTalkDto);

        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }


    @Test
    void shouldCreateTalkAndSpeakerCorrectly_NoCopy() {
        //Arrange
        var primarySpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().withCompany("Tst AG").build();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(primarySpeakerRequest);

        //requires .from(...) methods for all builders
        var coSpeakerRequest = CreateSpeakerRequestBuilder.from(primarySpeakerRequest)
                .withName("Sec Undo")
                .withEmail("sec.undo@example.com").build();

        SpeakerDto savedCoSpeakerDto = speakerService.createSpeaker(coSpeakerRequest);

        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest()
                .withPrimarySpeaker(savedSpeakerDto)
                .withCoSpeakers(List.of(savedCoSpeakerDto))
                .build();

        //Act
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        //Assert
        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto, expectedTalkDto);

        assertThat(savedTalkDto.coSpeakers())
                .hasSize(1)
                .containsExactly(savedCoSpeakerDto);

        assertThat(Set.of(savedTalkDto.primarySpeaker().company(), savedCoSpeakerDto.company())).contains("Tst AG");



    }
}
