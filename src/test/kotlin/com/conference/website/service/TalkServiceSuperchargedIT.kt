package com.conference.website.service

import com.conference.website.api.dto.*
import com.conference.website.domain.TalkLevel
import com.conference.website.dto.TestDtoConversions
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
@Transactional
class TalkServiceSuperchargedIT @Autowired constructor(
   private val speakerService: SpeakerService,
    private val tagService: TagService,
    private val talkService: TalkService,
) {

    @Test
    fun `should create speaker and talk`() {
        val createSpeakerRequest = CreateSpeakerRequest(
            "Ada Lovelace",
            "ada@example.com",
            "Analytical Engines",
            "Pioneer in computing"
        )
        val savedSpeakerDto = speakerService.createSpeaker(createSpeakerRequest)
        Assertions.assertThat<SpeakerDto>(savedSpeakerDto).isNotNull()

        val createTalkRequest = CreateTalkRequest(
            "Supercharging JVM tests",
            "Practical patterns to reduce noisy test code",
            TalkLevel.ADVANCED,
            60,
            savedSpeakerDto.email,
            mutableListOf<String>(),
            mutableListOf<String>(),
            ScheduleSlotRequest(
                "Room B",
                LocalDateTime.of(2026, 4, 8, 14, 0),
                LocalDateTime.of(2026, 4, 8, 15, 0)
            )
        )


        val savedTalkDto = talkService.createTalk(createTalkRequest)

        val expectedTalkDto = TestDtoConversions.toDto(
            savedTalkDto.id,
            TestDtoConversions.toDto(savedSpeakerDto.id, createSpeakerRequest),
            TestDtoConversions.toDto(savedTalkDto.scheduleSlot.id, createTalkRequest.scheduleSlot),
            createTalkRequest
        ) //talkService.getTalk(assertThat(savedTalkDto).isNotNull().actual().id());

        val talks = talkService.listTalks()
        assert(savedTalkDto == expectedTalkDto &&
                talks.size == 2
        )
    }


}

//https://youtrack.jetbrains.com/projects/KTIJ/issues/KTIJ-32562/Power-assert-compiler-plugin-cant-be-used-by-JPS-if-imported-from-a-maven-based-project
