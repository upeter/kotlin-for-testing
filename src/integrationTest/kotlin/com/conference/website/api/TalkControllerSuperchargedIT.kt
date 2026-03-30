package com.conference.website.api

import com.conference.website.domain.TalkLevel
import com.conference.website.dsl.speaker
import com.conference.website.dsl.talk
import com.conference.website.dsl.talks
import com.conference.website.dsl.undoDataScope
import com.conference.website.dsl.withNewTransaction
import com.conference.website.dto.TalkDto
import com.conference.website.repository.RepositorySupport
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import com.conference.website.utils.defaultHeaders
import com.conference.website.utils.readBody
import io.kotest.matchers.collections.shouldContainAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

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
    fun `should hide uncommitted talks and show them after commit using dsl and scope`() = undoDataScope {
        //Arrange
        val uniqueSuffix = System.nanoTime().toString()

        val speaker = speaker {
            name = "Ada Lovelace"
            email = "ada.$uniqueSuffix@example.com"
            company = "Analytical Engines"
            bio = "Pioneer in computing"
        }.persistWithPostUndo()

        val talks = talks {
            talk {
                title = "RestTestClient kotlin boundary first: $uniqueSuffix"
                abstractText = "DSL fixtures stay readable"
                level = TalkLevel.INTERMEDIATE
                durationMinutes = 45
                primarySpeaker(speaker)
            }
            talk {
                title = "RestTestClient kotlin boundary second: $uniqueSuffix"
                abstractText = "Transaction boundaries via HTTP"
                level = TalkLevel.ADVANCED
                durationMinutes = 60
                primarySpeaker(speaker)
            }
        }.persistWithPostUndo()

        buildList {
            add("countdown:")
            addAll((10 downTo 1).map { " $it" })
        }

        //Act
        val repliedTalks =
        withNewTransaction {
            restTestClient.get()
                .uri("/api/talks")
                .defaultHeaders()
                .exchangeSuccessfully()
                .expectStatus().isOk()
                .readBody<List<TalkDto>>()
        }

        //Assert
        repliedTalks.map { it.title }
            .shouldContainAll(talks.map { it.title })
    }


}
