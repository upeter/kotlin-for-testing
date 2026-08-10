package com.conference.website.api

import com.conference.website.domain.Speaker
import com.conference.website.domain.Talk
import com.conference.website.domain.TalkLevel
import com.conference.website.dto.TalkDto
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TalkRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Transactional
@ActiveProfiles("it")
class E02_TalkControllerIT {

    @Autowired
    private lateinit var restTestClient: RestTestClient

    @Autowired
    private lateinit var talkRepository: TalkRepository

    @Autowired
    private lateinit var speakerRepository: SpeakerRepository

    @Test
    fun shouldNotSeeUncommittedTalksViaRestTestClientButSeeThemAfterCommit() {
        //Arrange
        //one nullable id per fixture, declared up front so the finally block can see them
        var speakerId: Long? = null
        var firstTalkId: Long? = null
        var secondTalkId: Long? = null

        val uniqueSuffix = System.nanoTime().toString()
        try {
            val primarySpeaker = Speaker(
                "Ada Lovelace $uniqueSuffix",
                "ada.$uniqueSuffix@example.com",
                "Analytical Engines",
                "Pioneer in computing"
            )

            val firstTalk = Talk(
                "RestTestClient transaction boundary - first - $uniqueSuffix",
                "Learn Kotlin in 20 minutes",
                TalkLevel.INTERMEDIATE,
                20,
                primarySpeaker
            )

            val secondTalk = Talk(
                "RestTestClient transaction boundary - second - $uniqueSuffix",
                "Learn Kotlin in 20 minutes",
                TalkLevel.ADVANCED,
                20,
                primarySpeaker
            )

            //the save order is on us: the speaker has to exist before the talks
            speakerRepository.save(primarySpeaker)
            val talks = talkRepository.saveAll(listOf(firstTalk, secondTalk))
            talkRepository.flush()

            speakerId = primarySpeaker.id
            firstTalkId = talks.first().id
            secondTalkId = talks.last().id

            //We MUST commit the transaction before we can see the talks via the REST client
            TestTransaction.flagForCommit()
            TestTransaction.end()

            //Act
            val repliedTalks = restTestClient.get()
                .uri("/api/talks")
                .exchangeSuccessfully()
                .expectStatus().isOk()
                .returnResult(object : ParameterizedTypeReference<List<TalkDto>>() {})
                .responseBody

            //Assert
            assertThat(repliedTalks)
                .extracting<String>(TalkDto::title)
                .contains(*talks.map { it.title }.toTypedArray())
        } finally {
            //MUST cleanup, otherwise the next test will fail
            if (firstTalkId != null && talkRepository.existsById(firstTalkId)) {
                talkRepository.deleteById(firstTalkId)
            }
            if (secondTalkId != null && talkRepository.existsById(secondTalkId)) {
                talkRepository.deleteById(secondTalkId)
            }
            if (speakerId != null && speakerRepository.existsById(speakerId)) {
                speakerRepository.deleteById(speakerId)
            }
        }
    }
}
