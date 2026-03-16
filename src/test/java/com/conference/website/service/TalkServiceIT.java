package com.conference.website.service;

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

import static com.conference.website.dto.DtoConversions.toDto;
import static org.assertj.core.api.Assertions.assertThat;
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

        var talks = talkService.listTalks();
        assertEquals(2, talks.size());


        assertEquals(savedTalkDto, expectedTalkDto);
        assertThat(savedTalkDto.ratings()).isEmpty();




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
        var createSpeakerRequest = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().build();
        SpeakerDto savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest);

        //show that if withPrimarySpeaker is not invoked an error is thrown
        var createTalkRequest = CreateTalkRequestBuilder.aCreateTalkRequest().withPrimarySpeaker(savedSpeakerDto).build();
        var savedTalkDto = talkService.createTalk(createTalkRequest);

        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), createTalkRequest);
        assertEquals(savedTalkDto, expectedTalkDto);

        var talks = talkService.listTalks();
        assertEquals(1, talks.size());

    }
}
