package com.conference.website.api

import com.conference.website.data.createRatingDto
import com.conference.website.data.createScheduleSlotDto
import com.conference.website.data.createSpeakerDto
import com.conference.website.data.createTagDto
import com.conference.website.data.createTalkDto
import com.conference.website.data.createTalkRequest
import com.conference.website.dto.TalkDto
import com.conference.website.service.TalkService
import com.conference.website.service.ViewTrackingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

@WebMvcTest(TalkController::class)
@Import(ApiExceptionHandler::class)
class TalkControllerSuperchargedIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var talkService: TalkService

    @MockitoBean
    lateinit var viewTrackingService: ViewTrackingService

    @Test
    fun shouldCreateTalkComparingDtos() {
        val primarySpeaker = createSpeakerDto(id = 1L, company = "Tst AG")
        val coSpeaker = createSpeakerDto(id = 2L, name = "Joe ", email = "joe@example.com", company = "Tst AG")
        val talkRequest = createTalkRequest(
            primarySpeaker = primarySpeaker,
            coSpeakers = listOf(coSpeaker),
            tags = listOf(createTagDto(id = 1L, name = "java"))
        )

        val createdTalk = createTalkDto(request = talkRequest,
            ratings = listOf(createRatingDto(id = 1L, reviewerName = "Test Reviewer", score = 5, comment = "Excellent talk")),
            scheduleSlot = createScheduleSlotDto(),
            averageRating = 5.0,
            totalRatings = 1L
        )

        given(talkService.createTalk(talkRequest)).willReturn(createdTalk)

        val responseBody = mockMvc.perform(
            post("/api/talks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(talkRequest))
        )
            .andExpect(status().isCreated)
            .andReturn()
            .response
            .contentAsString

        val actualTalk = objectMapper.readValue(responseBody, TalkDto::class.java)
        assertThat(actualTalk).usingRecursiveComparison().isEqualTo(createdTalk)
    }
}
