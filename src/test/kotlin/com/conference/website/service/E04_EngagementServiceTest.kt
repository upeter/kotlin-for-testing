package com.conference.website.service

import com.conference.website.domain.TalkLevel
import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.EngagementCountDto
import com.conference.website.dto.EngagementUpdateRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest
class E04_EngagementServiceTest {

    @Autowired
    private lateinit var speakerService: SpeakerService

    @Autowired
    private lateinit var talkService: TalkService

    @Autowired
    private lateinit var engagementService: EngagementService

    @Test
    fun shouldRecordEngagementAndReadCounts() {
        //Arrange
        val createSpeakerRequest = CreateSpeakerRequest(
            "Ada Lovelace",
            "ada@example.com",
            "Analytical Engines",
            "Pioneer in computing"
        )
        val savedSpeakerDto = speakerService
            .createSpeaker(createSpeakerRequest)
        val createTalkRequest = CreateTalkRequest(
            "Modern JVM testing",
            "How to build robust and maintainable tests",
            TalkLevel.INTERMEDIATE,
            45,
            savedSpeakerDto
        )
        val talk = talkService.createTalk(createTalkRequest)

        val engagement1 = EngagementUpdateRequest(true, true, false)
        val engagement2 = EngagementUpdateRequest(false, true, true)

        //Act
        StepVerifier.create( //<- complex testing abstraction
            Mono.zip( //<- Mono magic to combine multiple Mono
                engagementService.recordEngagement(talk.id!!, engagement1),
                engagementService.recordEngagement(talk.id!!, engagement2)
            )
                .flatMap { recorded ->
                    engagementService.getCurrentEngagement(talk.id!!)
                        .map { current ->
                            //accumulated results from nested calls
                            listOf(recorded.t1, recorded.t2, current)
                        }
                }
        )
            //Assert
            .assertNext { result ->
                assertThat(result).hasSize(3)
                val current: EngagementCountDto = result[2] //what is 2?
                assertThat(current.views).isEqualTo(1L)
                assertThat(current.likes).isEqualTo(2L)
                assertThat(current.attends).isEqualTo(1L)
            }
            .verifyComplete() //<- won't run if verifyComplete() is not called
    }
}
