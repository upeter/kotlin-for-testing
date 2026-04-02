package com.conference.website.service

import com.conference.website.data.createScheduleSlotDto
import com.conference.website.data.createSpeakerDto
import com.conference.website.data.createTalkDto
import com.conference.website.data.createTalkRequest
import com.conference.website.domain.TalkLevel
import com.conference.website.dsl.speaker
import com.conference.website.dsl.talks
import com.conference.website.repository.RepositorySupport
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import io.kotest.matchers.collections.shouldContainAllInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Transactional
class E03_TalkServiceSuperchargedTest @Autowired constructor(
    private val talkService: TalkService,
    override val speakerRepository: SpeakerRepository,
    override val tagRepository: TagRepository,
    override val talkRepository: TalkRepository,
    ) : RepositorySupport {

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
                    bio = "Created Linux"
                }
                tag("spring")
            }
        }.persistGraph()






        //Act
        val talks = talkService.listTalks().sortedBy { it.title }

        //Assert
        talks shouldHaveSize 2
        talks.map { it.title }
            .shouldContainAllInAnyOrder("Kotlin DSL Power", "Spring Testing at Scale")

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