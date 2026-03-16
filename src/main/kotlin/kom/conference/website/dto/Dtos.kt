package kom.conference.website.dto

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
data class TalkDto(
    val id: Long,
    val title: String,
    val abstractText: String,
    val level: TalkLevel,
    val durationMinutes: Int,
    val primarySpeaker: SpeakerDto,
    val coSpeakers: List<SpeakerDto>,
    val tags: List<TagDto>,
    val ratings: List<RatingDto>,
    val scheduleSlot: ScheduleSlotDto?,
    val averageRating: Double,
    val totalRatings: Long,
)

@JvmRecord
data class CreateTagsRequest(
    @field:NotEmpty val names: List<String>,
)

@JvmRecord
data class CreateTalkRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val abstractText: String,
    @field:NotNull val level: TalkLevel,
    @field:NotNull @field:Min(5) val durationMinutes: Int,
    @field:Valid val primarySpeaker: SpeakerDto,
    val coSpeakers: List<@Valid SpeakerDto>,
    val tags: List<@Valid TagDto>,
)

@JvmRecord
data class ScheduleSlotDto(
    val id: Long,
    @field:NotBlank val roomName: String,
    @field:NotNull val startTime: LocalDateTime,
    @field:NotNull val endTime: LocalDateTime,
)

@JvmRecord
data class RatingDto(
    val id: Long,
    val reviewerName: String,
    val score: Int,
    val comment: String,
)

@JvmRecord
data class TagDto(
    val id: Long,
    val name: String,
)

@JvmRecord
data class SpeakerDto(
    val id: Long,
    val name: String,
    val email: String,
    val company: String,
    val bio: String,
)

@JvmRecord
data class ViewCountResponse(
    val talkId: Long,
    val views: Long,
)

@JvmRecord
data class ScheduleSlotRequest(
    @field:NotBlank val roomName: String,
    @field:NotNull val startTime: LocalDateTime,
    @field:NotNull val endTime: LocalDateTime,
)

@JvmRecord
data class CreateSpeakerRequest(
    @field:NotBlank val name: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank val company: String,
    @field:NotBlank val bio: String,
)

@JvmRecord
data class CreateRatingRequest(
    @field:NotBlank val reviewerName: String,
    @field:NotNull @field:Min(1) @field:Max(5) val score: Int,
    @field:NotBlank val comment: String,
)
