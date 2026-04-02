package com.conference.website.api

import com.conference.website.data.createTagDto
import com.conference.website.data.createTagsRequest
import com.conference.website.dto.CreateTagsRequest
import com.conference.website.service.TagService
import com.conference.website.utils.authorizationHeader
import com.conference.website.utils.defaultHeaders
import com.conference.website.utils.jsonContent
import com.conference.website.utils.objectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.mockk.every
import kom.conference.website.dto.TagDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.core.type.TypeReference
import java.util.List

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
@WebMvcTest(TagController::class)
@Import(ApiExceptionHandler::class)
@AutoConfigureMockMvc
class E06_TagControllerSuperchargedTest @Autowired constructor (
    val mockMvc: MockMvc,
    @MockkBean
    val tagService: TagService,
) {

    @Test
    fun `POST to tags should create tags`() {
        //Arrange
        val request = createTagsRequest("java", "kotlin")
        val expectedTags = request.names.mapIndexed { index, tag ->
            createTagDto(index.toLong(), name = tag)
        }

        every { tagService.createTags(request) } returns expectedTags

        //Act
        val response = mockMvc.perform(post("/api/tags")
            .defaultHeaders(token = "my-token")
            .jsonContent(request)
        )
        .andExpect(status().isCreated)
        .andReturn()
        .response
        .contentAsString

        val createdTags = objectMapper
            .readValue(response,
                object : TypeReference<List<TagDto>>() {})

        //Assert
        createdTags.map { it.name } shouldContainInOrder request.names
    }






    @Test
    fun `should list tags`() {
        //Arrange
        val initialTags = listOf("java", "testing")
        val expectedTags = initialTags.mapIndexed { index, tag ->  createTagDto(index.toLong(), name = tag)  }
        every { tagService.allTags } returns expectedTags

        //Act
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/tags")
                .header("X-Correlation-Id", "1234567890")
                .header("Authorization", "Bearer token")
        )
            .andExpect(status().isOk())
            .andReturn()
            .response
            .contentAsString

        val tags = objectMapper.readValue(response, object : TypeReference<List<TagDto>>() {})
        //Assert
        tags.map { it.name } shouldContainInOrder initialTags
    }

    @Test
    fun `should return bad request when names is empty`() {
        val request = CreateTagsRequest(emptyList())

        val response = mockMvc.perform(
            post("/api/tags")
                .header("X-Correlation-Id", "1234567890")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest())
            .andReturn()
            .response
            .contentAsString

        val errorResponse = objectMapper.readValue(response, ProblemDetail::class.java)
        errorResponse.apply {
            title shouldBe "Validation failure"
            detail shouldBe "Request validation failed"
        }
    }

}
