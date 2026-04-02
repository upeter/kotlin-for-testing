package com.conference.website.api

import com.conference.website.domain.TalkLevel
import com.conference.website.dsl.speaker
import com.conference.website.dsl.talks
import com.conference.website.dsl.testDataScope
import com.conference.website.dsl.withNewTransaction
import com.conference.website.dto.TalkDto
import com.conference.website.repository.RepositorySupport
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import com.conference.website.utils.defaultHeaders
import com.conference.website.utils.readBody
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Transactional
@ActiveProfiles("it")
class TalksControllerSuperchargedIT @Autowired constructor(
    private val restTestClient: RestTestClient,
    override  val speakerRepository: SpeakerRepository,
    override  val talkRepository: TalkRepository,
    override val tagRepository: TagRepository,
): RepositorySupport {

    @Test
    fun `should see committed talks only`() = testDataScope {
        //Arrange
        val talks = withNewTransaction {
            val uniqueSuffix = System.nanoTime().toString()
            val speaker = speaker {
                name = "Ada Lovelace"
                email = "ada.$uniqueSuffix@example.com"
            }.persistWithUndo() //<- guaranteed cleanup

            talks {
                talk {
                    title = "RestTestClient kotlin boundary first: $uniqueSuffix"
                    level = TalkLevel.INTERMEDIATE
                    primarySpeaker(speaker)
                }
                talk {
                    title = "RestTestClient kotlin boundary second: $uniqueSuffix"
                    level = TalkLevel.ADVANCED
                    primarySpeaker(speaker)
                }
            }.persistWithUndo() //<- guaranteed cleanup
        }

        //Act
        val repliedTalks = restTestClient.get().uri("/api/talks")
            .defaultHeaders()
            .exchangeSuccessfully()
            .expectStatus().isOk()
            .readBody<List<TalkDto>>()

        //Assert
        repliedTalks.map { it.title }
            .shouldContainAllInAnyOrder(talks.map { it.title })


    }


}





















