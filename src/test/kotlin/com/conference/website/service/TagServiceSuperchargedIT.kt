package com.conference.website.service

import com.conference.website.api.dto.CreateTagRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class TagServiceSuperchargedIT(
    @Autowired private val tagService: TagService,
) {

    @Test
    fun `should reject duplicate tag names with supercharged assertions`() {
        tagService.createTag(CreateTagRequest("kotlin")).apply {
            id.shouldNotBeNull()
            name shouldBe "kotlin"
        }
//        val createdTag = tagService.createTag(CreateTagRequest("kotlin"))
//        createdTag.id.shouldNotBeNull()
//        createdTag.name shouldBe "kotlin"

        shouldThrow<BadRequestException> {
            tagService.createTag(CreateTagRequest("kotlin"))
        }.message //.shouldNotBeNull()
            .shouldContainInOrder("Tag already exists", "kotlin")
    }
}
