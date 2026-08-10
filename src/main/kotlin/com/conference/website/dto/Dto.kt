package com.conference.website.dto

import com.conference.website.domain.EvaluationStatus
import com.conference.website.domain.TalkLevel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant
import java.time.LocalDateTime

@JvmRecord
data class SpeakerDto(
    val id: Long?,
    val name: String,
    val email: String,
    val company: String,
    val bio: String
)

@JvmRecord
data class TagDto(
    val id: Long?,
    val name: String
)

@JvmRecord
data class ScheduleSlotDto(
    val id: Long?,
    @field:NotBlank val roomName: String,
    @field:NotNull val startTime: LocalDateTime,
    @field:NotNull val endTime: LocalDateTime
)

@JvmRecord
data class RatingDto(
    val id: Long?,
    val reviewerName: String,
    val score: Int,
    val comment: String? = null
//    val createdAt: Instant
)

@JvmRecord
data class ModerationMessageDto(
    val id: Long?,
    val evaluatorName: String,
    val message: String,
    val createdAt: Instant?
)

@JvmRecord
data class TalkDto @JvmOverloads constructor (
    val id: Long?,
    val title: String,
    val abstractText: String,
    val level: TalkLevel,
    val durationMinutes: Int,
//    val createdAt: Instant,
    val primarySpeaker: SpeakerDto,
    val coSpeakers: List<SpeakerDto>,
    val tags: List<TagDto>,
    val ratings: List<RatingDto>,
    val scheduleSlot: ScheduleSlotDto?,
    val averageRating: Double,
    val totalRatings: Long,
    val evaluationStatus: EvaluationStatus = EvaluationStatus.SUBMITTED,
)

@JvmRecord
data class EngagementCountDto(
    val talkId: Long?,
    val views: Long?,
    val likes: Long?,
    val attends: Long?
)

@JvmRecord
data class ViewCountDto(
    val talkId: Long?,
    val views: Long?
)
