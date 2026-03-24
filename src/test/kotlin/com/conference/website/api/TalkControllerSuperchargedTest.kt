package com.conference.website.api

import com.conference.website.data.createScheduleSlotDto
import com.conference.website.data.createSpeakerDto
import com.conference.website.data.createTalkDto
import com.conference.website.data.createTalkRequest
import com.conference.website.dto.TalkDto
import com.conference.website.service.TalkService
import com.conference.website.service.ViewTrackingService
import com.conference.website.utils.defaultHeaders
import com.conference.website.utils.jsonContent
import com.conference.website.utils.objectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.conference.website.utils.readBody
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest(TalkController::class)
@Import(ApiExceptionHandler::class)
@AutoConfigureMockMvc
class TalkControllerSuperchargedTest @Autowired constructor (
    val mockMvc: MockMvc,
    @MockkBean
    val talkService: TalkService,
    @MockkBean
    val viewTrackingService: ViewTrackingService,
) {


    @Test
    fun `POST should create talk`() {
        //Arrange
        val primarySpeaker = createSpeakerDto(id = 1L, company = "Tst AG")
        val coSpeaker = createSpeakerDto(id = 2L, name = "Joe ", email = "joe@example.com", company = "Tst AG")
        val talkRequest = createTalkRequest(primarySpeaker = primarySpeaker, coSpeakers = listOf(coSpeaker))
        val createdTalk = createTalkDto(request = talkRequest, scheduleSlot = createScheduleSlotDto())

        every { talkService.createTalk(talkRequest) } returns createdTalk

        //Act
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
        //Assert
        actualTalk shouldBe createdTalk
    }

    @Test
    fun `POST should create talk with custom Extensions`() {
        //Arrange
        val primarySpeaker = createSpeakerDto(id = 1L, company = "Tst AG")
        val coSpeaker = createSpeakerDto(id = 2L, name = "Joe ", email = "joe@example.com", company = "Tst AG")
        val talkRequest = createTalkRequest(primarySpeaker = primarySpeaker, coSpeakers = listOf(coSpeaker))
        val createdTalk = createTalkDto(request = talkRequest, scheduleSlot = createScheduleSlotDto())

        every { talkService.createTalk(talkRequest) } returns createdTalk

        //Act
        val response = mockMvc.perform(
            post("/api/talks")
                .defaultHeaders(correltionId = "23232323")
                .jsonContent(talkRequest))
                .andExpect(status().isCreated)
                .andReturn()
                .response
                .contentAsString

        val actualTalk = objectMapper.readValue(response, TalkDto::class.java)

        //Assert
        actualTalk shouldBe createdTalk
    }

    @Test
    fun `POST should create talk with DSL`() {
        //Arrange
        val primarySpeaker = createSpeakerDto(id = 1L, company = "Tst AG")
        val coSpeaker = createSpeakerDto(id = 2L, name = "Joe ", email = "joe@example.com", company = "Tst AG")
        val talkRequest = createTalkRequest(primarySpeaker = primarySpeaker, coSpeakers = listOf(coSpeaker))
        val createdTalk = createTalkDto(request = talkRequest, scheduleSlot = createScheduleSlotDto())

        every { talkService.createTalk(talkRequest) } returns createdTalk

        //Act
        val actualTalk = mockMvc.perform(
            post("/api/talks")
                .defaultHeaders(correltionId = "23232323")
                .jsonContent(talkRequest)
        ).andExpect(status().isCreated)
            .readBody<TalkDto>()

        //Assert
        actualTalk shouldBe createdTalk
    }
}
