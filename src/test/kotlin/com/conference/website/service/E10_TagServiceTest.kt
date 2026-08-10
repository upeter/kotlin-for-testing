package com.conference.website.service

import com.conference.website.dto.CreateTagsRequest
import com.conference.website.dto.TagDto
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class E10_TagServiceTest {

    @Autowired
    private lateinit var tagService: TagService

    @Test
    fun shouldCreateTag() {
        //Arrange, Act
        val createdTags: List<TagDto> = tagService.createTags(
            CreateTagsRequest(listOf("Java"))
        )

        //Assert
        assertEquals(2, createdTags.size)
        val firstTag = createdTags.first()
        assertEquals("java", firstTag.name)
        assertNotNull(firstTag.id)
    }



















    @Test
    fun shouldRejectDuplicateTagNames() {
        val createdTags: List<TagDto> = tagService
            .createTags(
                CreateTagsRequest(
                    listOf("java", "kotlin")
                )
            )

        assertThat(createdTags)
            .hasSize(3)
            .allSatisfy { tag -> assertThat(tag.id).isNotNull() }
            .extracting<String>(TagDto::name)
            .containsExactlyInAnyOrder("java", "kotlin")

        assertThatThrownBy {
            tagService.createTags(CreateTagsRequest(listOf("Java")))
        }
            .isInstanceOf(BadRequestException::class.java)
            .hasMessageContaining("Tag already exists")
            .hasMessageContaining("java")
    }
}
