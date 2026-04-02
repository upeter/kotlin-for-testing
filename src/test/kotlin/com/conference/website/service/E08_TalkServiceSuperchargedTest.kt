package com.conference.website.service

import com.conference.website.data.createTalkRequest
import com.conference.website.domain.TalkLevel
import com.conference.website.repository.RepositorySupport
import com.conference.website.dsl.talks
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.TestDtoConversions
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kom.conference.website.data.createSpeakerRequest
import kom.conference.website.dto.CreateSpeakerRequest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class E08_TalkServiceSuperchargedTest @Autowired constructor(
    private val speakerService: SpeakerService,
    private val talkService: TalkService,
    override val speakerRepository: SpeakerRepository,
    override val tagRepository: TagRepository,
    override val talkRepository: TalkRepository,
) : RepositorySupport {


    @Test
    fun `should create speaker and talk with object mother`() {
        //Arrange
        val createSpeakerRequest = createSpeakerRequest(
            name = "Jack Vanilla",
            email = "jva@example.com"
        )
        val savedSpeakerDto = speakerService
                .createSpeaker(createSpeakerRequest)

        //safe approach, because the speaker is required,
        // which in a builder cannot be enforced
        val createTalkRequest = createTalkRequest(
                primarySpeaker = savedSpeakerDto,
                durationMinutes = 20)

        //Act
        val savedTalkDto = talkService.createTalk(createTalkRequest)

        //Assert
        //a bit clumsy
        val expectedTalkDto = TestDtoConversions.toDto(savedTalkDto.id, createTalkRequest)

        val talks = talkService.listTalks()
        assert(savedTalkDto == expectedTalkDto &&
                talks.size == 1
        )
    }


    @Test
    fun `should create multiple talks with local scoped dsl blocks`() {
        talks {
            talk {
                title = "Kotlin DSL Power"
                abstractText = "Scope fixtures without temporary variables"
                level = TalkLevel.INTERMEDIATE
                durationMinutes = 45
                primarySpeaker {
                    name = "Ada Lovelace"
                    email = "ada@lovelace.com"
                    company = "Analytical Engines"
                    bio = "Pioneer in computing"
                }
                coSpeaker {
                    name = "Grace Hopper"
                }
                tags("kotlin", "testing")
            }
            talk {
                title = "Spring Testing at Scale"
                abstractText = "Keep setup readable while growing scenarios"
                level = TalkLevel.ADVANCED
                durationMinutes = 60
                primarySpeaker {
                    name = "Linus Torvalds"
                    email = "linus@example.com"
                    company = "Kernel Inc"
                    bio = "Created Linux"
                }
                tag("spring")
            }
        }.persistGraph()

        //Act
        val talks = talkService.listTalks().sortedBy { it.title }

        //Assert
        talks shouldHaveSize 2
        talks.map { it.title }.shouldContainAllInAnyOrder("Kotlin DSL Power", "Spring Testing at Scale")

        talks.let { (firstTalk, lastTalk) ->
            firstTalk.apply {
                primarySpeaker.name shouldBe "Ada Lovelace"
                primarySpeaker.email shouldBe "ada@lovelace.com"
                coSpeakers.map { it.name }.shouldContainAllInAnyOrder("Grace Hopper")
                tags.map { it.name }.shouldContainAllInAnyOrder("kotlin", "testing")
            }

            lastTalk.apply {
                primarySpeaker.name shouldBe "Linus Torvalds"
                primarySpeaker.email shouldBe "linus@example.com"
                tags.map { it.name }.shouldContainAllInAnyOrder("spring")
            }
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