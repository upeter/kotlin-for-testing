package kom.conference.website.data

import com.conference.website.domain.TalkLevel
import kom.conference.website.dto.CreateRatingRequest
import kom.conference.website.dto.CreateSpeakerRequest
import kom.conference.website.dto.CreateTagsRequest
import kom.conference.website.dto.CreateTalkRequest
import kom.conference.website.dto.RatingDto
import kom.conference.website.dto.ScheduleSlotRequest
import kom.conference.website.dto.SpeakerDto
import kom.conference.website.dto.TagDto
import kom.conference.website.dto.ViewCountResponse
import java.time.LocalDateTime


fun createSpeakerDto(
    id: Long? = null,
    name: String = "Ada Lovelace",
    email: String = "ada@example.com",
    company: String? = "Analytical Engines",
    bio: String = "Pioneer in computing"
) = SpeakerDto(
    id, name, email, company, bio,
)


fun createTagsRequest(vararg names: String = arrayOf("java")) =
    CreateTagsRequest( names.toList())


fun createTagDto(id: Long? = null, name: String = "java") =
    TagDto(id, name)


fun createTalkRequest(
    primarySpeaker: SpeakerDto,
    title: String = "Kotlin for Java Developers",
    abstractText: String = "Learn Kotlin in 20 minutes",
    level: TalkLevel = TalkLevel.BEGINNER,
    durationMinutes: Int = 20,
    coSpeakers: List<SpeakerDto> = emptyList(),
    tags: List<TagDto> = emptyList()
) = CreateTalkRequest(title, abstractText, level, durationMinutes, primarySpeaker, coSpeakers, tags)


fun createRatingDto(
    id: Long? = null,
    reviewerName: String = "Test Reviewer",
    score: Int = 5,
    comment: String? = "Excellent talk"
) = RatingDto(id, reviewerName, score, comment)


fun createViewCountResponse(
    talkId: Long = 1L,
    views: Long = 100L
) = ViewCountResponse(talkId, views)


fun createScheduleSlotRequest(
    roomName: String = "Main Hall",
    startTime: LocalDateTime = LocalDateTime.of(2026, 3, 16, 9, 0),
    endTime: LocalDateTime = LocalDateTime.of(2026, 3, 16, 10, 0)
) = ScheduleSlotRequest(roomName, startTime, endTime)


fun createSpeakerRequest(
    name: String = "Ada Lovelace",
    email: String = "ada@example.com",
    bio: String = "Pioneer in computing",
    company: String? = "Analytical Engines"
) = CreateSpeakerRequest(name, email,  bio, company)


fun createRatingRequest(
    reviewerName: String = "Test Reviewer",
    score: Int = 5,
    comment: String? = null
) = CreateRatingRequest(reviewerName, score, comment)
