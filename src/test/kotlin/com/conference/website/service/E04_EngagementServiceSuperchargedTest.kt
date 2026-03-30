package com.conference.website.service

import com.conference.website.data.createSpeakerRequest
import com.conference.website.data.createTalkRequest
import com.conference.website.dto.EngagementCountDto
import com.conference.website.dto.EngagementUpdateRequest
import com.conference.website.utils.awaitAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.transaction.Transactional
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class E04_EngagementServiceSuperchargedTest @Autowired constructor(
    private val speakerService: SpeakerService,
    private val talkService: TalkService,
    private val engagementService: EngagementService,
) {

    @Test
    fun `should submit views likes and attends together with coroutines`() = runTest {
        //Arrange
        val speaker = speakerService.createSpeaker(createSpeakerRequest())
        val talk = talkService.createTalk(
            createTalkRequest(primarySpeaker = speaker))

        //Act
        val payloads = listOf(
            EngagementUpdateRequest(true, true, false),
            EngagementUpdateRequest(false, true, true),
        )

        val recordedEngagements = payloads.map {
            engagementService.recordEngagement(talk.id, it) }
            .awaitAll()
        val currentEngagement = engagementService
            .getCurrentEngagement(talk.id).awaitSingle()

        //Assert
        recordedEngagements shouldHaveSize 2
        currentEngagement shouldBe EngagementCountDto(talk.id, 1L, 2L, 1L)
    }

}
