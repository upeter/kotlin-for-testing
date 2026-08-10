package com.conference.website.service

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
class E07_TalkServiceTest {

    @Autowired
    private lateinit var speakerService: SpeakerService

    @Autowired
    private lateinit var talkService: TalkService

    @Test
    fun shouldCreateTalkAndSpeakerCorrectly_NoCopy() {
        //Arrange
        val primarySpeakerRequest = CreateSpeakerRequest(
            "Ada Lovelace",
            "ada@example.com",
            "Tst AG",
            "Pioneer in computing"
        )

        //no copy(): every field that stays the same has to be spelled out again,
        //and every field that is added later has to be added here too
        val coSpeakerRequest = CreateSpeakerRequest(
            "Sec Undo",
            "sec.undo@example.com",
            primarySpeakerRequest.company,
            primarySpeakerRequest.bio
        )

        val savedSpeakerDto: SpeakerDto = speakerService
            .createSpeaker(primarySpeakerRequest)
        val savedCoSpeakerDto: SpeakerDto = speakerService
            .createSpeaker(coSpeakerRequest)

        val createTalkRequest = CreateTalkRequest(
            "Modern JVM testing",
            "How to build robust and maintainable tests",
            TalkLevel.INTERMEDIATE,
            45,
            savedSpeakerDto,
            listOf(savedCoSpeakerDto)
        )

        //Act
        val savedTalkDto = talkService.createTalk(createTalkRequest)

        //Assert
        val expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id, createTalkRequest)
        assertEquals(savedTalkDto.primarySpeaker, expectedTalkDto.primarySpeaker)
        assertThat(savedTalkDto.coSpeakers)
            .hasSize(1)
            .containsExactly(savedCoSpeakerDto)
        assertThat(listOf(savedTalkDto.primarySpeaker.company, savedCoSpeakerDto.company)).contains("Tst AG")
    }
}
