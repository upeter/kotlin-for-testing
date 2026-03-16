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
import com.conference.website.utils.jsonContent
import com.conference.website.utils.objectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import com.conference.website.utils.readBody
import com.conference.website.utils.toJson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(TalkController::class)
@Import(ApiExceptionHandler::class)
class TalkControllerSuperchargedIT(
    @Autowired
    val mockMvc: MockMvc,
    @MockkBean
    val talkService: TalkService,
) {

    @Test
    fun `POST should create talk`() {
        val primarySpeaker = createSpeakerDto(id = 1L, company = "Tst AG")
        val coSpeaker = createSpeakerDto(id = 2L, name = "Joe ", email = "joe@example.com", company = "Tst AG")
        val talkRequest = createTalkRequest(primarySpeaker = primarySpeaker, coSpeakers = listOf(coSpeaker))
        val createdTalk = createTalkDto(
            request = talkRequest, scheduleSlot = createScheduleSlotDto()
        )

        every { talkService.createTalk(talkRequest) } returns createdTalk

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
        //excluion with
        actualTalk shouldBeEqualUsingFields {
            //excludedProperties = setOf(TalkDto::primarySpeaker.name, TalkDto::coSpeakers.name, TalkDto::tags.name)
            createdTalk
        }
        //assertThat(actualTalk).usingRecursiveComparison().isEqualTo(createdTalk)
    }

    @Test
    fun `POST should create talk with DSL`() {
        val primarySpeaker = createSpeakerDto(id = 1L, company = "Tst AG")
        val coSpeaker = createSpeakerDto(id = 2L, name = "Joe ", email = "joe@example.com", company = "Tst AG")
        val talkRequest = createTalkRequest(primarySpeaker = primarySpeaker, coSpeakers = listOf(coSpeaker))
        val createdTalk = createTalkDto(
            request = talkRequest, scheduleSlot = createScheduleSlotDto()
        )

        every { talkService.createTalk(talkRequest) } returns createdTalk

        val actualTalk = mockMvc.perform(
            post("/api/talks")
                .jsonContent(talkRequest)
        ).andExpect(status().isCreated)
            .readBody<TalkDto>()

        //excluion with
        actualTalk shouldBeEqualUsingFields {
            //excludedProperties = setOf(TalkDto::primarySpeaker.name, TalkDto::coSpeakers.name, TalkDto::tags.name)
            createdTalk
        }
        //assertThat(actualTalk).usingRecursiveComparison().isEqualTo(createdTalk)
    }
}
