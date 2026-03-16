package com.conference.website.service

import com.conference.website.api.dto.CreateTagRequest
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class TagServiceSuperchargedIT @Autowired constructor(
     private val tagService: TagService,
) {

    @Test
    fun `should reject duplicate tag names with supercharged assertions`() {
        assertSoftly {
            tagService.createTag(CreateTagRequest("kotlin")).apply {
                id.shouldNotBeNull()
                name shouldBe "kotlin"
            }
        }
        shouldThrow<BadRequestException> {
            tagService.createTag(CreateTagRequest("kotlin"))
        }.message.let { message ->
            assertTrue(
                message != null &&
                    "Tag already exists" in message &&
                    "kotlin" in message,
            )
        }
    }
}
