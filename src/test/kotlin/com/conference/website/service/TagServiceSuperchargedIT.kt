package com.conference.website.service

import com.conference.website.dto.CreateTagsRequest
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
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
        //assertSoftly can wrap multiple assertion blocks
        assertSoftly {

            tagService.createTags(CreateTagsRequest(listOf("java", "kotlin", "testing"))).apply {
                //rely on standard collection methds
                map { it.name }.shouldContainInOrder("java", "kotlin", "testing")
                forEach { it.id.shouldNotBeNull() }
            }

            //String is a collection
            shouldThrow<BadRequestException> {
                tagService.createTags(CreateTagsRequest(listOf("kotlin")))
            }.message.shouldContainInOrder("Tag already exists", "kotlin")
        }

    }

    @Test
    fun `should reject duplicate tag single names with supercharged assertions`() {
        assertSoftly {
            tagService.createTags(CreateTagsRequest(listOf("kotlin"))).first().apply {
                id.shouldNotBeNull()
                name shouldBe "kotlin"
            }
        }
        shouldThrow<BadRequestException> {
            tagService.createTags(CreateTagsRequest(listOf("kotlin")))
        }.message.let { message ->
            assertTrue(
                message != null &&
                        "Tag already exists" in message &&
                        "kotlin" in message,
            )
        }
    }
}
