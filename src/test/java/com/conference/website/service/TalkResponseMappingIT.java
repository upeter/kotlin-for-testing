package com.conference.website.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TalkResponseMappingIT {

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TalkService talkService;

//
//    @Test
//    void shouldMapTalkToNestedResponseWithDerivedFields() {
//        Speaker primarySpeaker = speakerService.createSpeaker(new CreateSpeakerRequest(
//                "Ada Lovelace",
//                "ada@example.com",
//                "Analytical Engines",
//                "Pioneer in computing"
//        ));
//
//        Speaker coSpeaker = speakerService.createSpeaker(new CreateSpeakerRequest(
//                "Grace Hopper",
//                "grace@example.com",
//                "US Navy",
//                "Invented modern compiler foundations"
//        ));
//
//        Tag javaTag = tagService.createTag(new CreateTagRequest("java"));
//        Tag architectureTag = tagService.createTag(new CreateTagRequest("architecture"));
//
//        Talk createdTalk = talkService.createTalk(new CreateTalkRequest(
//                "Modern JVM testing",
//                "How to build robust and maintainable tests",
//                TalkLevel.INTERMEDIATE,
//                45,
//                primarySpeaker.getId(),
//                List.of(coSpeaker.getId()),
//                List.of(javaTag.getId(), architectureTag.getId()),
//                new ScheduleSlotRequest(
//                        "Main Hall",
//                        LocalDateTime.of(2026, 3, 20, 10, 0),
//                        LocalDateTime.of(2026, 3, 20, 10, 45)
//                )
//        ));
//
//        talkService.addRating(createdTalk.getId(), new CreateRatingRequest("Alex", 5, "Excellent depth"));
//        talkService.addRating(createdTalk.getId(), new CreateRatingRequest("Sam", 3, "Solid but could use more demos"));
//
//        Talk reloadedTalk = talkService.getTalk(createdTalk.getId());
//        TalkDto response = DtoConversions.toDto(reloadedTalk);
//
//        assertThat(response.id()).isEqualTo(createdTalk.getId());
//        assertThat(response.title()).isEqualTo("Modern JVM testing");
//        assertThat(response.abstractText()).isEqualTo("How to build robust and maintainable tests");
//        assertThat(response.level()).isEqualTo(TalkLevel.INTERMEDIATE);
//        assertThat(response.durationMinutes()).isEqualTo(45);
//        assertThat(response.createdAt()).isNotNull();
//
//        assertThat(response.primarySpeaker()).isNotNull();
//        assertThat(response.primarySpeaker().id()).isEqualTo(primarySpeaker.getId());
//        assertThat(response.primarySpeaker().name()).isEqualTo("Ada Lovelace");
//        assertThat(response.primarySpeaker().email()).isEqualTo("ada@example.com");
//        assertThat(response.primarySpeaker().company()).isEqualTo("Analytical Engines");
//        assertThat(response.primarySpeaker().bio()).isEqualTo("Pioneer in computing");
//
//        assertThat(response.coSpeakers())
//                .hasSize(1)
//                .extracting("id", "name", "email", "company", "bio")
//                .containsExactly(tuple(
//                        coSpeaker.getId(),
//                        "Grace Hopper",
//                        "grace@example.com",
//                        "US Navy",
//                        "Invented modern compiler foundations"
//                ));
//
//        assertThat(response.tags())
//                .extracting("name")
//                .containsExactlyInAnyOrder("java", "architecture");
//
//        assertThat(response.scheduleSlot()).isNotNull();
//        assertThat(response.scheduleSlot().roomName()).isEqualTo("Main Hall");
//        assertThat(response.scheduleSlot().startTime()).isEqualTo(LocalDateTime.of(2026, 3, 20, 10, 0));
//        assertThat(response.scheduleSlot().endTime()).isEqualTo(LocalDateTime.of(2026, 3, 20, 10, 45));
//
//        assertThat(response.ratings()).hasSize(2);
//        assertThat(response.ratings())
//                .extracting("reviewerName", "score", "comment")
//                .containsExactlyInAnyOrder(
//                        tuple("Alex", 5, "Excellent depth"),
//                        tuple("Sam", 3, "Solid but could use more demos")
//                );
//        assertThat(response.ratings()).allSatisfy(rating -> {
//            assertThat(rating.id()).isNotNull();
//            assertThat(rating.createdAt()).isNotNull();
//        });
//
//        assertThat(response.averageRating()).isEqualTo(4.0d);
//        assertThat(response.totalRatings()).isEqualTo(2L);
//    }
}
