package com.conference.website.dto

import com.conference.website.domain.EvaluationStatus
import com.conference.website.domain.TalkLevel
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@JvmRecord
data class CreateSpeakerRequest(
    @field:NotBlank val name: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank val company: String,
    @field:NotBlank val bio: String
)

@JvmRecord
data class CreateTagsRequest(
    @field:NotEmpty val names: List<String>
)

@JvmRecord
data class ScheduleSlotRequest(
    @field:NotBlank val roomName: String,
    @field:NotNull val startTime: LocalDateTime,
    @field:NotNull val endTime: LocalDateTime
)

@JvmRecord
data class CreateTalkRequest @JvmOverloads constructor(
    @field:NotBlank val title: String,
    @field:NotBlank val abstractText: String,
    @field:NotNull val level: TalkLevel,
    @field:NotNull @field:Min(5) val durationMinutes: Int,
    @field:Valid val primarySpeaker: SpeakerDto,
    val coSpeakers: List<@Valid SpeakerDto> = emptyList(),
    val tags: List<@Valid TagDto> = emptyList(),
    val ratings: List<@Valid RatingDto> = emptyList(),
//    @field:Valid val scheduleSlot: ScheduleSlotDto
)
@JvmRecord
data class CreateRatingRequest(
    @field:NotBlank val reviewerName: String,
    @field:NotNull @field:Min(1) @field:Max(5) val score: Int,
    val comment: String? = null
)

@JvmRecord
data class CreateModerationMessageRequest(
    @field:NotBlank val evaluatorName: String,
    @field:NotBlank val message: String
)

@JvmRecord
data class EngagementUpdateRequest(
    val view: Boolean,
    val like: Boolean,
    val attend: Boolean
)

@JvmRecord
data class UpdateEvaluationStatusRequest(
    @field:NotNull val status: EvaluationStatus
)
