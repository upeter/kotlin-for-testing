package com.conference.website.dto

import com.conference.website.domain.Rating
import com.conference.website.domain.ScheduleSlot
import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag

/**
 * The inferior conversion helper: one overload per source type, all crammed into a
 * single static-style holder. Kotlin would rather have extension functions on the
 * types themselves (see `dto/DtoConversions.kt` in main) — that is the point of the
 * contrast.
 */
object TestDtoConversions {

    fun toDto(id: Long?, request: CreateSpeakerRequest) =
        SpeakerDto(id, request.name, request.email, request.company, request.bio)

    fun toDto(speaker: Speaker) =
        SpeakerDto(speaker.id, speaker.name, speaker.email, speaker.company, speaker.bio)

    fun toDto(tag: Tag) = TagDto(tag.id, tag.name)

    fun toDto(rating: Rating) =
        RatingDto(rating.id, rating.reviewerName, rating.score, rating.comment)

    fun toDto(id: Long?, request: ScheduleSlotRequest) =
        ScheduleSlotDto(id, request.roomName, request.startTime, request.endTime)

    fun toDto(slot: ScheduleSlot) =
        ScheduleSlotDto(slot.id, slot.roomName, slot.startTime, slot.endTime)

    fun toDto(id: Long?, request: CreateTalkRequest) =
        TalkDto(
            id,
            request.title,
            request.abstractText,
            request.level,
            request.durationMinutes,
            request.primarySpeaker,
            request.coSpeakers,
            request.tags,
            emptyList(),
            null,
            0.0,
            0L
        )
}
