package com.conference.website.service

import com.conference.website.dto.CreateTagsRequest
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
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
class TagServiceSuperchargedTest @Autowired constructor(
    private val tagService: TagService,
) {

    @Test
    fun `should create tag`() {
        val createdTags = tagService.createTags(CreateTagsRequest(mutableListOf("Kotlin")))
        createdTags shouldHaveSize 1
        createdTags.first().apply {
            name shouldBe "kotlin"
            id.shouldNotBeNull()
        }
    }

    @Test
    fun `should reject duplicate tag names with supercharged assertions`() {
        //assertSoftly can wrap multiple assertion blocks
        assertSoftly {

            tagService.createTags(CreateTagsRequest(listOf("Java", "kotlin", "Testing"))).apply {
                //rely on standard collection methds
                size shouldBe 3
                forEach { it.id.shouldNotBeNull() }
                map { it.name }.shouldContainInOrder("java", "kotlin", "testing")
            }

            //String is a collection too
            shouldThrow<BadRequestException> {
                tagService.createTags(CreateTagsRequest(listOf("Kotlin")))
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
