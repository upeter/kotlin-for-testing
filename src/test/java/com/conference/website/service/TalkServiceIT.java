package com.conference.website.service;

import com.conference.website.api.dto.*;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.TestDtoConversions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.conference.website.api.dto.DtoConversions.toDto;
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
        assertThat(savedSpeakerDto).isNotNull();

        var createTalkRequest = new CreateTalkRequest(
                "Supercharging JVM tests",
                "Practical patterns to reduce noisy test code",
                TalkLevel.ADVANCED,
                60,
                savedSpeakerDto.email(),
                List.of(),
                List.of(),
                new ScheduleSlotRequest(
                        "Room B",
                        LocalDateTime.of(2026, 4, 8, 14, 0),
                        LocalDateTime.of(2026, 4, 8, 15, 0)
                )
        );


        var savedTalkDto = talkService.createTalk(createTalkRequest);

        var expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id(), TestDtoConversions.toDto(savedSpeakerDto.id(), createSpeakerRequest), TestDtoConversions.toDto(savedTalkDto.scheduleSlot().id(), createTalkRequest.scheduleSlot()), createTalkRequest);//talkService.getTalk(assertThat(savedTalkDto).isNotNull().actual().id());

        var talks = talkService.listTalks();
        assertEquals(2, talks.size());


        assertEquals(savedTalkDto, expectedTalkDto);




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



    }
}
