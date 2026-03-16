package kom.conference.website.dto

import com.conference.website.domain.TalkLevel
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime


@JvmRecord
data class TalkDto(
    val id: Long? = null,
    val title: String,
    val abstractText: String,
    val level: TalkLevel,
    val durationMinutes: Int,
    val primarySpeaker: SpeakerDto,
    val coSpeakers: List<SpeakerDto> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    val ratings: List<RatingDto> = emptyList(),
    val scheduleSlot: ScheduleSlotDto? = null,
    val averageRating: Double = ratings.map { it.score }.average(),
    val totalRatings: Long = ratings.size.toLong()
)

@JvmRecord
data class CreateTagsRequest(
    @field:NotEmpty val names: List<String>,
)

@JvmRecord
data class CreateTalkRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val abstractText: String,
    val level: TalkLevel,
    @field:Min(5) val durationMinutes: Int,
    @field:Valid val primarySpeaker: SpeakerDto,
    val coSpeakers: List<@Valid SpeakerDto> = emptyList(),
    val tags: List<@Valid TagDto> = emptyList(),
)

@JvmRecord
data class ScheduleSlotDto(
    val id: Long? = null,
    @field:NotBlank val roomName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)

@JvmRecord
data class RatingDto(
    val id: Long? = null,
    val reviewerName: String,
    val score: Int,
    val comment: String? = null,
)

@JvmRecord
data class TagDto(
    val id: Long? = null,
    val name: String,
)

@JvmRecord
data class SpeakerDto(
    val id: Long? = null,
    val name: String,
    val email: String,
    val company: String? = null,
    val bio: String,
)

@JvmRecord
data class ViewCountResponse(
    val talkId: Long? = null,
    val views: Long? = null,
)

@JvmRecord
data class ScheduleSlotRequest(
    @field:NotBlank val roomName: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)

@JvmRecord
data class CreateSpeakerRequest(
    @field:NotBlank val name: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank val bio: String,
    @field:NotBlank val company: String? = null,
)

@JvmRecord
data class CreateRatingRequest(
    @field:NotBlank val reviewerName: String,
    @field:Min(1) @field:Max(5) val score: Int,
    @field:NotBlank val comment: String? = null,
)
