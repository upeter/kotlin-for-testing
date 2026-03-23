package com.conference.website.service

import kom.conference.website.data.createSpeakerRequest
import com.conference.website.data.createTalkRequest
import com.conference.website.dto.TestDtoConversions
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class TalkServiceSuperchargedDataClassIT @Autowired constructor(
   private val speakerService: SpeakerService,
    private val talkService: TalkService,
) {

    @Test
    fun `should create speaker and talk with object mother`() {
        //Arrange
        val primarySpeakerRequest = createSpeakerRequest(company = "Tst AG",)
        //copy approach: very flexible
        val coSpeakerRequest = primarySpeakerRequest.copy(name = "Sec Undo", email = "sec.undo@example.com")
        //Collection spice
        val(primarySpeakerDto, coSpeakerDto) = listOf(primarySpeakerRequest, coSpeakerRequest).map(speakerService::createSpeaker)

        //save approach, because the speaker is required, which in a builder cannot be enforced
        val createTalkRequest = createTalkRequest(primarySpeaker = primarySpeakerDto, coSpeakers = listOf(coSpeakerDto))

        //Act
        val savedTalkDto = talkService.createTalk(createTalkRequest)

        //Assert
        assertSoftly {
            savedTalkDto.primarySpeaker() shouldBe primarySpeakerDto
            savedTalkDto.coSpeakers().apply {
                size shouldBe 1
                first() shouldBe coSpeakerDto
            }
            setOf(savedTalkDto.primarySpeaker().company, coSpeakerDto.company) shouldContain "Tst AG"
        }
    }


}

//https://youtrack.jetbrains.com/projects/KTIJ/issues/KTIJ-32562/Power-assert-compiler-plugin-cant-be-used-by-JPS-if-imported-from-a-maven-based-project
/**
 *             ScheduleSlotRequest(
 *                 "Room B",
 *                 LocalDateTime.of(2026, 4, 8, 14, 0),
 *                 LocalDateTime.of(2026, 4, 8, 15, 0)
 *             )
 *
 */