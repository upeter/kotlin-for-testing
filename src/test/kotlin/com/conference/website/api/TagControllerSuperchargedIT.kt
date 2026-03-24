package com.conference.website.api

import com.conference.website.data.createTagDto
import com.conference.website.data.createTagsRequest
import com.conference.website.dto.TalkDto
import com.conference.website.service.TagService
import com.conference.website.utils.defaultHeaders
import com.conference.website.utils.jsonContent
import com.conference.website.utils.readBody
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.List

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
@WebMvcTest(TagController::class)
@Import(ApiExceptionHandler::class)
@AutoConfigureMockMvc
class TagControllerSuperchargedIT @Autowired constructor (
    val mockMvc: MockMvc,
    @MockkBean
    val tagService: TagService,
) {

    @Test
    fun `POST to tags should create tags`() {
        //Arrange
        val tagRequest = createTagsRequest("java", "kotlin")
        val expectedTags = tagRequest.names.mapIndexed { index, tag ->  createTagDto(index.toLong(), name = tag)  }
        every { tagService.createTags(tagRequest) } returns expectedTags

        //Act
        val createdTags = mockMvc.perform(post("/api/tags")
                .defaultHeaders()
                .jsonContent(tagRequest))
            .andExpect(status().isCreated)
            .readBody<List<TagDto>>()

        //Assert
        createdTags.map { it.name } shouldContainInOrder tagRequest.names
    }

}
