package com.conference.website.api

import com.conference.website.domain.Speaker
import com.conference.website.domain.Talk
import com.conference.website.domain.TalkLevel
import com.conference.website.dto.TalkDto
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TalkRepository
import com.conference.website.utils.E02_EntityLifecycleTestUtils
import com.conference.website.utils.E02_TransactionTestUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Transactional
@ActiveProfiles("it")
class E02_TalkControllerAdvancedIT {

    @Autowired
    private lateinit var restTestClient: RestTestClient

    @Autowired
    private lateinit var talkRepository: TalkRepository

    @Autowired
    private lateinit var speakerRepository: SpeakerRepository

    @Test
    fun shouldUseHigherOrderUtilitiesToVerifyVisibilityBeforeAndAfterCommit() {
        val uniqueSuffix = System.nanoTime().toString()
        val firstTitle = "RestTestClient advanced boundary - first - $uniqueSuffix"
        val secondTitle = "RestTestClient advanced boundary - second - $uniqueSuffix"

        val speaker = Speaker(
            "Grace Hopper $uniqueSuffix",
            "grace.$uniqueSuffix@example.com",
            "Analytical Engines",
            "Pioneer in computing"
        )

        //cleanup is tied to nesting: every fixture adds one more level of indentation
        E02_EntityLifecycleTestUtils.doWithSpeaker(speakerRepository, speaker) { savedSpeaker ->
            val firstTalk = Talk(
                firstTitle,
                "Learn Kotlin in 20 minutes",
                TalkLevel.INTERMEDIATE,
                20,
                savedSpeaker
            )

            E02_EntityLifecycleTestUtils.doWithTalk(talkRepository, firstTalk) {
                val secondTalk = Talk(
                    secondTitle,
                    "Learn Kotlin in 20 minutes",
                    TalkLevel.ADVANCED,
                    20,
                    savedSpeaker
                )

                E02_EntityLifecycleTestUtils.doWithTalk(talkRepository, secondTalk) {
                    val talksAfterCommit = E02_TransactionTestUtils.withNewTransaction {
                        restTestClient.get()
                            .uri("/api/talks")
                            .header("X-Transaction-Timeout", "1000")
                            .header("Authorization", "Bearer token")
                            .exchangeSuccessfully()
                            .expectStatus().isOk()
                            .returnResult(object : ParameterizedTypeReference<List<TalkDto>>() {})
                            .responseBody
                    }

                    assertThat(talksAfterCommit)
                        .extracting<String>(TalkDto::title)
                        .contains(firstTitle, secondTitle)
                }
            }
        }
    }
}
