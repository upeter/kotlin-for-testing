package com.conference.website.service

import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag
import com.conference.website.domain.Talk
import com.conference.website.domain.TalkLevel
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.TagDto
import com.conference.website.dto.TalkDto
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class E03_TalkServiceTest {

    @Autowired
    private lateinit var speakerRepository: SpeakerRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @Autowired
    private lateinit var talkRepository: TalkRepository

    @Autowired
    private lateinit var talkService: TalkService

    @Test
    fun shouldCreateMultipleTalksWithTemporaryVariables() {
        //Arrange
        val primarySpeakerTalkOne = Speaker(
            "Ada Lovelace",
            "ada@example.com",
            "Analytical Engines",
            "Pioneer in computing"
        )

        val coSpeakerTalkOne = Speaker(
            "Grace Hopper",
            "grace@example.com",
            primarySpeakerTalkOne.company, //<- copy a field by hand
            "COBOL pioneer"
        )

        val primarySpeakerTalkTwo = Speaker(
            "Linus Torvalds",
            "linus@example.com",
            "Kernel Inc",
            "Created Linux"
        )

        val kotlinTag = Tag("kotlin")

        val testingTag = Tag("testing")

        val springTag = Tag("spring")

        val talkEntityOne = Talk(
            "Kotlin DSL Power",
            "Scope fixtures without temporary variables",
            TalkLevel.INTERMEDIATE,
            45,
            primarySpeakerTalkOne
        )
        talkEntityOne.coSpeakers = linkedSetOf(coSpeakerTalkOne)
        talkEntityOne.tags = linkedSetOf(kotlinTag, testingTag)

        val talkEntityTwo = Talk(
            "Spring Testing at Scale",
            "Keep setup readable while growing scenarios",
            TalkLevel.ADVANCED,
            60,
            primarySpeakerTalkTwo
        )
        talkEntityTwo.tags = linkedSetOf(springTag)

        //every temporary variable has to be listed again, in the right order,
        //and it is on us to remember that speakers and tags come before talks
        speakerRepository.saveAll(
            listOf(primarySpeakerTalkOne, coSpeakerTalkOne, primarySpeakerTalkTwo)
        )
        tagRepository.saveAll(listOf(kotlinTag, testingTag, springTag))
        talkRepository.saveAll(listOf(talkEntityOne, talkEntityTwo))
        talkRepository.flush()


        //Act
        val createdTalks: List<TalkDto> = talkService.listTalks()



        //Assert
        val createdTalkOne = createdTalks.first()
        val createdTalkTwo = createdTalks.last()

        assertThat(createdTalks).hasSize(2)
        assertThat(createdTalks)
            .extracting<String>(TalkDto::title)
            .containsExactlyInAnyOrder("Kotlin DSL Power", "Spring Testing at Scale")

        assertThat(createdTalkTwo.primarySpeaker.name)
            .isEqualTo("Ada Lovelace")
        assertThat(createdTalkTwo.coSpeakers)
            .extracting<String>(SpeakerDto::name)
            .containsExactly("Grace Hopper")
        assertThat(createdTalkTwo.tags)
            .extracting<String>(TagDto::name)
            .containsExactlyInAnyOrder("kotlin", "testing")

        assertThat(createdTalkOne.primarySpeaker.name)
            .isEqualTo("Linus Torvalds")
        assertThat(createdTalkOne.tags)
            .extracting<String>(TagDto::name).containsExactly("spring")
    }
}
