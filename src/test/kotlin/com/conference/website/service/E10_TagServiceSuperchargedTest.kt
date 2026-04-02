package com.conference.website.service

import com.conference.website.dto.CreateTagsRequest
import com.conference.website.dto.TagDto
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
class E10_TagServiceSuperchargedTest @Autowired constructor(
    private val tagService: TagService,
) {




    @Test
    fun `should create tag`() {
        //Arrange, Act
        val createdTags = tagService.createTags(
            CreateTagsRequest(listOf("Kotlin")))

        //Assert
        createdTags shouldHaveSize 2
        createdTags.first().apply {
            name shouldBe "kotlin"
            id.shouldNotBeNull()
        }
    }



























    @Test
    fun `should reject duplicate tag names`() {

        assertSoftly {

            tagService.createTags(
                CreateTagsRequest(
                    listOf("Java", "kotlin", "Testing")
                )
            ).apply {
                //rely on standard collection methods
                this shouldHaveSize 3
                forEach { it.id.shouldNotBeNull() }
                map { it.name }.shouldContainInOrder("java", "kotlin", "testing")
            }


            //String is a collection too
            shouldThrow<BadRequestException> {
                tagService.createTags(CreateTagsRequest(listOf("Kotlin")))
            }.message.shouldContainInOrder("Tag already exists", "kotlin")

        }






    }






}
