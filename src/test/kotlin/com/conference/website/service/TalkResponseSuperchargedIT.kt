package com.conference.website.service

import com.conference.website.api.ConferenceApiMapper
import com.conference.website.api.dto.CreateRatingRequest
import com.conference.website.api.dto.CreateSpeakerRequest
import com.conference.website.api.dto.CreateTagRequest
import com.conference.website.api.dto.CreateTalkRequest
import com.conference.website.api.dto.ScheduleSlotRequest
import com.conference.website.api.dto.TalkResponse
import com.conference.website.domain.TalkLevel
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@Transactional
class TalkResponseSuperchargedIT @Autowired constructor(
   private val speakerService: SpeakerService,
    private val tagService: TagService,
    private val talkService: TalkService,
) {


    @Test
    fun `should map nested talk response with supercharged power assert`() {
        val primarySpeaker = speakerService.createSpeaker(
            CreateSpeakerRequest(
                "Linus Torvalds",
                "linus@example.com",
                "Linux Foundation",
                "Creator of Linux and Git",
            ),
        )
        val coSpeaker = speakerService.createSpeaker(
            CreateSpeakerRequest(
                "Sophie Wilson",
                "sophie@example.com",
                "Acorn Computers",
                "Designed the ARM instruction set",
            ),
        )
        val testingTag = tagService.createTag(CreateTagRequest("testing"))
        val kotlinTag = tagService.createTag(CreateTagRequest("kotlin"))

        val createdTalk = talkService.createTalk(
            CreateTalkRequest(
                "Supercharging JVM tests",
                "Practical patterns to reduce noisy test code",
                TalkLevel.ADVANCED,
                60,
                primarySpeaker.id,
                listOf(coSpeaker.id),
                listOf(testingTag.id, kotlinTag.id),
                ScheduleSlotRequest(
                    "Room B",
                    LocalDateTime.of(2026, 4, 8, 14, 0),
                    LocalDateTime.of(2026, 4, 8, 15, 0),
                ),
            ),
        )

        talkService.addRating(createdTalk.id, CreateRatingRequest("Taylor", 5, "Exactly what we needed"))
        talkService.addRating(createdTalk.id, CreateRatingRequest("Morgan", 4, "Great examples"))

        val response = ConferenceApiMapper.toTalkResponse(talkService.getTalk(createdTalk.id))

        normalize(response) shouldBe NormalizedTalkResponse(
            id = createdTalk.id,
            title = "Supercharging JVM tests",
            abstractText = "Practical patterns to reduce noisy test code",
            level = TalkLevel.ADVANCED,
            durationMinutes = 60,
            primarySpeaker = NormalizedSpeaker(
                id = primarySpeaker.id,
                name = "Linus Torvalds",
                email = "linus@example.com",
                company = "Linux Foundation",
                bio = "Creator of Linux and Git",
            ),
            coSpeakers = listOf(
                NormalizedSpeaker(
                    id = coSpeaker.id,
                    name = "Sophie Wilson",
                    email = "sophie@example.com",
                    company = "Acorn Computers",
                    bio = "Designed the ARM instruction set",
                ),
            ),
            tags = listOf("kotlin", "testing"),
            ratings = listOf(
                NormalizedRating("Morgan", 4, "Great examples"),
                NormalizedRating("Taylor", 5, "Exactly what we needed"),
            ),
            schedule = NormalizedSchedule(
                roomName = "Room B",
                start = LocalDateTime.of(2026, 4, 8, 14, 0),
                end = LocalDateTime.of(2026, 4, 8, 15, 0),
            ),
            averageRating = 4.5,
            totalRatings = 2,
        )
    }

    private fun normalize(response: TalkResponse): NormalizedTalkResponse =
        NormalizedTalkResponse(
            id = response.id(),
            title = response.title(),
            abstractText = response.abstractText(),
            level = response.level(),
            durationMinutes = response.durationMinutes(),
            primarySpeaker = NormalizedSpeaker(
                id = response.primarySpeaker().id(),
                name = response.primarySpeaker().name(),
                email = response.primarySpeaker().email(),
                company = response.primarySpeaker().company(),
                bio = response.primarySpeaker().bio(),
            ),
            coSpeakers = response.coSpeakers().map {
                NormalizedSpeaker(
                    id = it.id(),
                    name = it.name(),
                    email = it.email(),
                    company = it.company(),
                    bio = it.bio(),
                )
            }.sortedBy { it.name },
            tags = response.tags().map { it.name() }.sorted(),
            ratings = response.ratings().map {
                NormalizedRating(
                    reviewerName = it.reviewerName(),
                    score = it.score(),
                    comment = it.comment(),
                )
            }.sortedBy { it.reviewerName },
            schedule = NormalizedSchedule(
                roomName = response.scheduleSlot().roomName(),
                start = response.scheduleSlot().startTime(),
                end = response.scheduleSlot().endTime(),
            ),
            averageRating = response.averageRating(),
            totalRatings = response.totalRatings(),
        )
}

private data class NormalizedTalkResponse(
    val id: Long,
    val title: String,
    val abstractText: String,
    val level: TalkLevel,
    val durationMinutes: Int,
    val primarySpeaker: NormalizedSpeaker,
    val coSpeakers: List<NormalizedSpeaker>,
    val tags: List<String>,
    val ratings: List<NormalizedRating>,
    val schedule: NormalizedSchedule,
    val averageRating: Double,
    val totalRatings: Long,
)

private data class NormalizedSpeaker(
    val id: Long,
    val name: String,
    val email: String,
    val company: String,
    val bio: String,
)

private data class NormalizedRating(
    val reviewerName: String,
    val score: Int,
    val comment: String,
)

private data class NormalizedSchedule(
    val roomName: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
)
