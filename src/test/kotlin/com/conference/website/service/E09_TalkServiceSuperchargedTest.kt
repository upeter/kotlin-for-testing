package com.conference.website.service

import com.conference.website.data.createTalkRequest
import com.conference.website.domain.TalkLevel
import com.conference.website.repository.RepositorySupport
import com.conference.website.dsl.talks
import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.TestDtoConversions
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class E09_TalkServiceSuperchargedTest @Autowired constructor(
    private val speakerService: SpeakerService,
    private val talkService: TalkService)  {

    @Test
    fun `should create speaker and talk correctly`() {
        //Arrange
        val createSpeakerRequest = CreateSpeakerRequest(
            "Ada Lovelace",
            "ada@example.com",
            "Pioneer in computing",
            "Analytical Engines"
        )
        val savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest)

        val createTalkRequest = CreateTalkRequest(
            "Supercharging JVM tests",
            "Practical patterns to reduce noisy test code",
            TalkLevel.ADVANCED,
            60,
            savedSpeakerDto,
            mutableListOf(),
            mutableListOf(),
        )

        val savedTalkDto = talkService.createTalk(createTalkRequest)
        val expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id, createTalkRequest) //talkService.getTalk(assertThat(savedTalkDto).isNotNull().actual().id());

        //Act
        val talks = talkService.listTalks()

        //Assert
        assert(savedTalkDto == expectedTalkDto &&
                savedTalkDto.ratings.isEmpty() &&
                talks.size == 2
        )





















        //Reflection names
        assertThat(expectedTalkDto.primarySpeaker)
            .extracting(SpeakerDto::name.name, SpeakerDto::email.name,
                SpeakerDto::company.name, SpeakerDto::bio.name)
            .containsExactly(
                "Ada Lovelace",
                "ada@example.com",
                "Analytical Engines",
                "Pioneer in computing")

    }

}
