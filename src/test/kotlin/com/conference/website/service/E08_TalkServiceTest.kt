package com.conference.website.service

import com.conference.website.data.createSpeakerRequest
import com.conference.website.data.createTalkRequest
import com.conference.website.domain.TalkLevel
import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.TestDtoConversions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class E08_TalkServiceTest {

    @Autowired
    private lateinit var speakerService: SpeakerService

    @Autowired
    private lateinit var talkService: TalkService

    @Test
    fun shouldCreateTalkAndSpeakerCorrectly() {
        //Arrange
        val createSpeakerRequest = CreateSpeakerRequest(
            "Ada Lovelace",
            "ada@example.com",
            "Analytical Engines",
            "Pioneer in computing"
        )
        val savedSpeakerDto: SpeakerDto = speakerService.createSpeaker(createSpeakerRequest)

        val createTalkRequest = CreateTalkRequest(
            "Supercharging JVM tests",
            "Practical patterns to reduce noisy test code",
            TalkLevel.ADVANCED,
            60,
            savedSpeakerDto,
            emptyList(), //<- what is this list?
            emptyList()  //<- and this one?
        )
        val savedTalkDto = talkService.createTalk(createTalkRequest)
        val expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id, createTalkRequest)

        //Act
        val talks = talkService.listTalks()

        //Assert
        assertEquals(1, talks.size)
        assertEquals(savedTalkDto, expectedTalkDto)
        assertThat(savedTalkDto.ratings).isEmpty()

        assertThat(expectedTalkDto.primarySpeaker)
            .extracting("name", "email", "company", "bio") //<- strings, so no refactoring support
            .containsExactly(
                "Ada Lovelace",
                "ada@example.com",
                "Analytical Engines",
                "Pioneer in computing"
            )
    }













    @Test
    fun shouldCreateTalkAndSpeakerCorrectly_UsingKotlinMethods() {
        //Arrange
        val speakerRequest = createSpeakerRequest(
            "Jack Vanilla",
            "jva@example.com"
        )
        val savedSpeakerDto: SpeakerDto = speakerService
            .createSpeaker(speakerRequest)

        //the primary speaker is a required parameter,
        //so this cannot compile without it
        val createTalkRequest = createTalkRequest(savedSpeakerDto)

        //Act
        val savedTalkDto = talkService.createTalk(createTalkRequest)

        //Assert
        val expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id, createTalkRequest)
        assertEquals(savedTalkDto, expectedTalkDto)

        val talks = talkService.listTalks()
        assertEquals(1, talks.size)
    }
}
