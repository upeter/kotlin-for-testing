package com.conference.website.api

import com.conference.website.dto.CreateTagsRequest
import com.conference.website.dto.TagDto
import com.conference.website.service.TagService
import com.conference.website.utils.E06_MockMvcTestUtils.objectMapper
import com.conference.website.utils.E06_MockMvcTestUtils.performAndGetResponseWithHeaders
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.core.type.TypeReference

@WebMvcTest(TagController::class)
@Import(ApiExceptionHandler::class)
class E06_TagControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var tagService: TagService

    @Test
    fun shouldCreateTags() {
        //Arrange
        val request = CreateTagsRequest(
            listOf("java", "kotlin")
        )
        val expectedTags: List<TagDto> =
            request.names.indices
                .map { i ->
                    TagDto(i.toLong(), request.names[i])
                }

        given(tagService.createTags(request)).willReturn(expectedTags)

        //Act
        val response = mockMvc.perform(
            post("/api/tags")
                .header("X-Correlation-Id", "1234567890")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated())
            .andReturn()
            .response
            .contentAsString

        val createdTags = objectMapper.readValue(
            response,
            object : TypeReference<List<TagDto>>() {}
        )

        //Assert
        assertThat(createdTags)
            .extracting<String>(TagDto::name)
            .containsExactlyInAnyOrder(*request.names.toTypedArray())
    }



















    @Test
    fun shouldCreateTagsBetterQuestionMark() {
        //Arrange
        val request = CreateTagsRequest(listOf("java", "kotlin"))
        val tagDtos: List<TagDto> = request.names.indices
            .map { i -> TagDto(i.toLong(), request.names[i]) }
        given(tagService.createTags(request)).willReturn(tagDtos)

        //Act
        //a different overload than E05 picks — same arity, different parameter order
        val response = performAndGetResponseWithHeaders(
            mockMvc, "token",
            post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
            status().isOk()
        )

        val tags = objectMapper.readValue(response, object : TypeReference<List<TagDto>>() {})

        //Assert
        assertThat(tags)
            .extracting<String>(TagDto::name)
            .containsExactlyInAnyOrder(*request.names.toTypedArray())
    }

    @Test
    fun shouldListTags() {
        //Arrange
        given(tagService.getAllTags()).willReturn(listOf(TagDto(1L, "java"), TagDto(2L, "testing")))
        //Act
        val response = mockMvc.perform(
            get("/api/tags")
                .header("X-Correlation-Id", "1234567890")
                .header("Authorization", "Bearer token")
        )
            .andExpect(status().isOk())
            .andReturn()
            .response
            .contentAsString

        val tags = objectMapper.readValue(response, object : TypeReference<List<TagDto>>() {})
        //Assert
        assertThat(tags)
            .extracting<String>(TagDto::name)
            .containsExactlyInAnyOrder("java", "testing")
    }

    @Test
    fun shouldReturnBadRequestWhenNamesIsEmpty() {
        val request = CreateTagsRequest(listOf())

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
        assertEquals("Validation failure", errorResponse.title)
        assertEquals("Request validation failed", errorResponse.detail)
    }
}
