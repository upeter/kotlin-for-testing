package com.conference.website.service

import com.conference.website.domain.ModerationMessage
import com.conference.website.domain.Rating
import com.conference.website.domain.ScheduleSlot
import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag
import com.conference.website.domain.Talk
import com.conference.website.domain.TalkLevel
import com.conference.website.dto.*
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TagRepository
import com.conference.website.repository.TalkRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale

@Service
class TalkService(
    private val talkRepository: TalkRepository,
    private val speakerRepository: SpeakerRepository,
    private val tagRepository: TagRepository
) {

    @Transactional
    fun createTalk(request: CreateTalkRequest): TalkDto {
        val primarySpeaker = speakerRepository.findByIdOrNull(request.primarySpeaker.id!!)
            ?: throw NotFoundException("Primary speaker not found: ${request.primarySpeaker}")

        val coSpeakers = resolveCoSpeakers(request.coSpeakers, request.primarySpeaker)
        val tags = resolveTags(request.tags)

        val talk = Talk(
            request.title,
            request.abstractText,
            request.level,
            request.durationMinutes,
            primarySpeaker
        )

        talk.coSpeakers = coSpeakers
        talk.tags = tags

        return talkRepository.save(talk).toDto()
    }

    @Transactional(readOnly = true)
    fun listTalks(level: TalkLevel?, tag: String?): List<TalkDto> {
        if (level != null) {
            return talkRepository.findByLevel(level).map { it.toDto() }
        }
        if (tag != null && tag.isNotBlank()) {
            return talkRepository.findByTagsNameIgnoreCase(tag).map { it.toDto() }
        }
        return talkRepository.findAllByOrderByCreatedAtDesc().map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun listTalks(): List<TalkDto> =
        talkRepository.findAllByOrderByCreatedAtDesc().map { it.toDto() }

    @Transactional(readOnly = true)
    fun getTalk(talkId: Long): TalkDto =
        talkRepository.findDetailedById(talkId)?.let { it.toDto() }
            ?: throw NotFoundException("Talk not found: $talkId")

    @Transactional
    fun addRating(talkId: Long, request: CreateRatingRequest): TalkDto {
        val talk = talkRepository.findDetailedById(talkId)
            ?: throw NotFoundException("Talk not found: $talkId")

        val rating = Rating(request.reviewerName, request.score, request.comment)
        talk.addRating(rating)
        return talkRepository.save(talk).toDto()
    }

    @Transactional
    fun assignSchedule(talkId: Long, request: ScheduleSlotRequest): TalkDto {
        val talk = talkRepository.findDetailedById(talkId)
            ?: throw NotFoundException("Talk not found: $talkId")

        talk.scheduleSlot = toScheduleSlot(request)
        return talkRepository.save(talk).toDto()
    }

    @Transactional
    fun updateEvaluationStatus(talkId: Long, request: UpdateEvaluationStatusRequest): TalkDto {
        val talk = talkRepository.findDetailedById(talkId)
            ?: throw NotFoundException("Talk not found: $talkId")

        talk.evaluationStatus = request.status
        return talkRepository.save(talk).toDto()
    }

    @Transactional
    fun addModerationMessage(talkId: Long, request: CreateModerationMessageRequest): TalkDto {
        val talk = talkRepository.findDetailedById(talkId)
            ?: throw NotFoundException("Talk not found: $talkId")

        val message = ModerationMessage(request.evaluatorName, request.message)
        talk.addModerationMessage(message)
        return talkRepository.save(talk).toDto()
    }

    private fun resolveCoSpeakers(
        coSpeakerDtos: List<SpeakerDto>?,
        primarySpeakerDto: SpeakerDto
    ): MutableSet<Speaker> {
        if (coSpeakerDtos.isNullOrEmpty()) {
            return LinkedHashSet()
        }
        val primarySpeaker = speakerRepository.findByIdOrNull(primarySpeakerDto.id!!)
            ?: throw NotFoundException("Primary speaker not found: ${primarySpeakerDto.id}")

        val coSpeakers = speakerRepository.findAllById(coSpeakerDtos.map { it.id!! })
        if (coSpeakers.size != coSpeakerDtos.size) {
            throw BadRequestException("One or more co-speaker are invalid")
        }
        if (coSpeakers.map { it.id }.contains(primarySpeaker.id)) {
            throw BadRequestException("Primary speaker cannot also be a co-speaker")
        }

        return LinkedHashSet(coSpeakers)
    }

    private fun resolveTags(tagNames: List<TagDto>?): MutableSet<Tag> {
        if (tagNames.isNullOrEmpty()) {
            return LinkedHashSet()
        }

        val tags = tagRepository.findAllById(tagNames.map { it.id!! })
        if (tagNames.size != tags.size) {
            throw BadRequestException("One or more tag names are invalid")
        }
        return LinkedHashSet(tags)
    }

    private fun normalizeValues(values: List<String>, fieldName: String): List<String> {
        val normalized = values.map { normalizeValue(it) }
        if (normalized.any { it.isBlank() }) {
            throw BadRequestException("One or more $fieldName are blank")
        }
        return ArrayList(LinkedHashSet(normalized))
    }

    private fun normalizeValue(value: String?): String =
        if (value == null) "" else value.trim().lowercase(Locale.ROOT)

    private fun toScheduleSlot(request: ScheduleSlotRequest?): ScheduleSlot? {
        if (request == null) {
            return null
        }

        if (!request.endTime.isAfter(request.startTime)) {
            throw BadRequestException("Schedule endTime must be after startTime")
        }

        return ScheduleSlot(request.roomName, request.startTime, request.endTime)
    }
}
