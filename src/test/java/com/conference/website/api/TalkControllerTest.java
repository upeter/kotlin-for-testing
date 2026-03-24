package com.conference.website.api;

import com.conference.website.data.builders.CreateTalkRequestBuilder;
import com.conference.website.data.builders.SpeakerDtoBuilder;
import com.conference.website.data.builders.TalkDtoBuilder;
import com.conference.website.dto.CreateRatingRequest;
import com.conference.website.dto.CreateTalkRequest;
import com.conference.website.dto.ScheduleSlotRequest;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.dto.TalkDto;
import com.conference.website.service.NotFoundException;
import com.conference.website.service.TalkEngagementService;
import com.conference.website.service.TalkService;
import com.conference.website.service.ViewTrackingService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static com.conference.website.utils.MockMvcTestUtils.performAndGetResponseWithHeaders;
import static com.conference.website.domain.TalkLevel.ADVANCED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TalkController.class)
@Import(ApiExceptionHandler.class)
@AutoConfigureMockMvc
class TalkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TalkService talkService;

    @MockitoBean
    private ViewTrackingService viewTrackingService;

    @MockitoBean
    private TalkEngagementService talkEngagementService;

    @Test
    void shouldCreateTalk() throws Exception {
        //Arrange
        SpeakerDto primarySpeaker = SpeakerDtoBuilder
                .aSpeakerDto()
                .withCompany("Tst AG")
                .build();

        var coSpeaker = SpeakerDtoBuilder.from(primarySpeaker)
                .withId(2L)
                .withName("Joe ")
                .withEmail("joe@example.com").build();

        TalkDto createdTalk = TalkDtoBuilder.aTalkDto()
                .withId(99L)
                .withPrimarySpeaker(primarySpeaker)
                .withCoSpeakers(List.of(coSpeaker))
                .build();

        CreateTalkRequest talkRequest = CreateTalkRequestBuilder.aCreateTalkRequest()
                .withPrimarySpeaker(primarySpeaker)
                .withCoSpeakers(List.of(coSpeaker))
                .build();

        given(talkService.createTalk(talkRequest)).willReturn(createdTalk);

        //Act & Assert
        mockMvc.perform(post("/api/talks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Correlation-Id", "1234567890")
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(talkRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.title").value(talkRequest.title()))
                .andExpect(jsonPath("$.abstractText").value(talkRequest.abstractText()))
                .andExpect(jsonPath("$.level").value(talkRequest.level().name()))
                .andExpect(jsonPath("$.durationMinutes").value(talkRequest.durationMinutes()))
                .andExpect(jsonPath("$.primarySpeaker.id").value(primarySpeaker.id()))
                .andExpect(jsonPath("$.primarySpeaker.name").value(primarySpeaker.name()))
                .andExpect(jsonPath("$.primarySpeaker.email").value(primarySpeaker.email()))
                .andExpect(jsonPath("$.primarySpeaker.company").value(primarySpeaker.company()))
                .andExpect(jsonPath("$.primarySpeaker.bio").value(primarySpeaker.bio()))
                .andExpect(jsonPath("$.coSpeakers[0].id").value(2))
                .andExpect(jsonPath("$.coSpeakers[0].name").value(coSpeaker.name()))
                .andExpect(jsonPath("$.coSpeakers[0].email").value(coSpeaker.email()))
                .andExpect(jsonPath("$.coSpeakers[0].company").value(coSpeaker.company()))
                .andExpect(jsonPath("$.coSpeakers[0].bio").value(coSpeaker.bio()))
                .andExpect(jsonPath("$.tags[0].id").exists())
                .andExpect(jsonPath("$.tags[0].name").exists())
                .andExpect(jsonPath("$.ratings[0].id").exists())
                .andExpect(jsonPath("$.ratings[0].score").exists())
                .andExpect(jsonPath("$.ratings[0].reviewerName").exists())
                .andExpect(jsonPath("$.scheduleSlot.roomName").exists())
                .andExpect(jsonPath("$.scheduleSlot.startTime").exists())
                .andExpect(jsonPath("$.scheduleSlot.endTime").exists())
                .andExpect(jsonPath("$.averageRating").value(5.0))
                .andExpect(jsonPath("$.totalRatings").value(1));
    }

    @Test
    void shouldCreateTalkComparingDtos() throws Exception {
        //Arrange
        SpeakerDto primarySpeaker = SpeakerDtoBuilder
                .aSpeakerDto()
                .withCompany("Tst AG")
                .build();

        SpeakerDto coSpeaker = SpeakerDtoBuilder.from(primarySpeaker)
                .withId(2L)
                .withName("Joe ")
                .withEmail("joe@example.com")
                .build();

        TalkDto createdTalk = TalkDtoBuilder.aTalkDto()
                .withId(99L)
                .withPrimarySpeaker(primarySpeaker)
                .withCoSpeakers(List.of(coSpeaker))
                .build();

        CreateTalkRequest talkRequest = CreateTalkRequestBuilder.aCreateTalkRequest()
                .withPrimarySpeaker(primarySpeaker)
                .withCoSpeakers(List.of(coSpeaker))
                .build();

        given(talkService.createTalk(talkRequest)).willReturn(createdTalk);

        //Act
//        String body = mockMvc.perform(post("/api/talks")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-Correlation-Id", "1234567890")
//                .header("Authorization", "Bearer token")
//                .content(objectMapper.writeValueAsString(talkRequest)))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();


        String body = performAndGetResponseWithHeaders("83473847", mockMvc,  post("/api/talks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(talkRequest)));

        TalkDto actualTalk = objectMapper.readValue(body, TalkDto.class);

        //Assert
        assertThat(actualTalk).usingRecursiveComparison().isEqualTo(createdTalk);
    }

    @Test
    void shouldListTalksWithFilters() throws Exception {
        List<TalkDto> talks = List.of(TalkDtoBuilder.aTalkDto().withId(7L).withLevel(ADVANCED).build());
        given(talkService.listTalks(ADVANCED, "java")).willReturn(talks);

        mockMvc.perform(get("/api/talks").param("level", "ADVANCED").param("tag", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
                .andExpect(jsonPath("$[0].level").value("ADVANCED"));
    }

    @Test
    void shouldGetTalkById() throws Exception {
        TalkDto talk = TalkDtoBuilder.aTalkDto().withId(3L).withTitle("Testing in production").build();
        given(talkService.getTalk(3L)).willReturn(talk);

        mockMvc.perform(get("/api/talks/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.title").value("Testing in production"));
    }

    @Test
    void shouldAddRating() throws Exception {
        CreateRatingRequest request = new CreateRatingRequest("Jane", 5, "Great");
        TalkDto talk = TalkDtoBuilder.aTalkDto().withId(5L).withTotalRatings(2L).build();
        given(talkService.addRating(5L, request)).willReturn(talk);

        mockMvc.perform(post("/api/talks/5/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.totalRatings").value(2));
    }

    @Test
    void shouldAssignSchedule() throws Exception {
        ScheduleSlotRequest request = new ScheduleSlotRequest(
                "Main Hall",
                LocalDateTime.of(2026, 4, 8, 10, 0),
                LocalDateTime.of(2026, 4, 8, 10, 45)
        );
        TalkDto talk = TalkDtoBuilder.aTalkDto().withId(11L).build();
        given(talkService.assignSchedule(11L, request)).willReturn(talk);

        mockMvc.perform(put("/api/talks/11/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.scheduleSlot.roomName").value("Main Hall"));
    }

    @Test
    void shouldGetCurrentViews() throws Exception {
        given(viewTrackingService.getCurrentViews(8L)).willReturn(Mono.just(17L));

        mockMvc.perform(get("/api/talks/8/views"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.talkId").value(8))
                .andExpect(jsonPath("$.views").value(17));
    }

    @Test
    void shouldReturnBadRequestWhenSimulateEventsIsInvalid() throws Exception {
        assertThatThrownBy(() -> mockMvc.perform(post("/api/talks/8/views/simulate").param("events", "0")))
                .hasRootCauseMessage("simulateViews.events: must be greater than or equal to 1");
    }

    @Test
    void shouldMapNotFoundExceptionTo404() throws Exception {
        given(talkService.getTalk(404L)).willThrow(new NotFoundException("Talk not found: 404"));

        mockMvc.perform(get("/api/talks/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Talk not found: 404"));
    }

    @Test
    void shouldDelegateListTalksWithoutFilters() throws Exception {
        given(talkService.listTalks(null, null)).willReturn(List.of());

        mockMvc.perform(get("/api/talks"))
                .andExpect(status().isOk());

        verify(talkService).listTalks(eq(null), eq(null));
        verify(talkService, org.mockito.Mockito.never()).createTalk(any());
    }
}
