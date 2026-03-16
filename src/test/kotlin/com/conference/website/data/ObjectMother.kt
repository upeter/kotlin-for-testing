package com.conference.website.data

import com.conference.website.domain.TalkLevel
import com.conference.website.dto.CreateRatingRequest
import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.RatingDto
import com.conference.website.dto.ScheduleSlotRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.TagDto
import com.conference.website.dto.ViewCountResponse
import kom.conference.website.dto.CreateTagsRequest
import java.time.LocalDateTime


@JvmOverloads
fun createSpeakerDto(
    id: Long? = null,
    name: String = "Ada Lovelace",
    email: String = "ada@example.com",
    company: String? = "Analytical Engines",
    bio: String = "Pioneer in computing"
) = SpeakerDto(
    id, name, email, company, bio,
)

@JvmOverloads
fun createTagsRequest(vararg names: String = arrayOf("java")) =
    CreateTagsRequest( names.toList())

@JvmOverloads
fun createTagDto(id: Long? = null, name: String = "java") =
    TagDto(id, name)

@JvmOverloads
fun createTalkRequest(
    primarySpeaker: SpeakerDto,
    title: String = "Kotlin for Java Developers",
    abstractText: String = "Learn Kotlin in 20 minutes",
    level: TalkLevel = TalkLevel.BEGINNER,
    durationMinutes: Int = 20,
    coSpeakers: List<SpeakerDto> = emptyList(),
    tags: List<TagDto> = emptyList()
) = CreateTalkRequest(title, abstractText, level, durationMinutes, primarySpeaker, coSpeakers, tags)

@JvmOverloads
fun createRatingDto(
    id: Long? = null,
    reviewerName: String = "Test Reviewer",
    score: Int = 5,
    comment: String? = "Excellent talk"
) = RatingDto(id, reviewerName, score, comment)

@JvmOverloads
fun createViewCountResponse(
    talkId: Long = 1L,
    views: Long = 100L
) = ViewCountResponse(talkId, views)

@JvmOverloads
fun createScheduleSlotRequest(
    roomName: String = "Main Hall",
    startTime: LocalDateTime = LocalDateTime.of(2026, 3, 16, 9, 0),
    endTime: LocalDateTime = LocalDateTime.of(2026, 3, 16, 10, 0)
) = ScheduleSlotRequest(roomName, startTime, endTime)

@JvmOverloads
fun createSpeakerRequest(
    name: String = "Ada Lovelace",
    email: String = "ada@example.com",
    company: String? = "Analytical Engines",
    bio: String = "Pioneer in computing"
) = CreateSpeakerRequest(name, email, company, bio)

@JvmOverloads
fun createRatingRequest(
    reviewerName: String = "Test Reviewer",
    score: Int = 5,
    comment: String? = null
) = CreateRatingRequest(reviewerName, score, comment)
