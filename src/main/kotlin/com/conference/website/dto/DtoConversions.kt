package com.conference.website.dto

import com.conference.website.domain.ModerationMessage
import com.conference.website.domain.Rating
import com.conference.website.domain.ScheduleSlot
import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag
import com.conference.website.domain.Talk

fun Speaker.toDto(): SpeakerDto =
    SpeakerDto(
        id,
        name,
        email,
        company,
        bio
    )

fun Tag.toDto(): TagDto = TagDto(id, name)

fun ScheduleSlot?.toScheduleSlotResponse(): ScheduleSlotDto? {
    if (this == null) {
        return null
    }

    return ScheduleSlotDto(
        id,
        roomName,
        startTime,
        endTime
    )
}

fun Talk.toDto(): TalkDto {
    val ratingDto = ratings
        .sortedByDescending { it.createdAt }
        .map { it.toDto() }

    val averageRating =
        if (ratings.isEmpty()) 0.0
        else ratings.map { it.score }.average()

    return TalkDto(
        id,
        title,
        abstractText,
        level,
        durationMinutes,
//                createdAt,
        primarySpeaker.toDto(),
        coSpeakers.map { it.toDto() },
        tags.map { it.toDto() },
        ratingDto,
        scheduleSlot.toScheduleSlotResponse(),
        averageRating,
        ratings.size.toLong(),
        evaluationStatus)
}

private fun Rating.toDto(): RatingDto =
    RatingDto(
        id,
        reviewerName,
        score,
        comment
        //createdAt
    )

private fun ModerationMessage.toDto(): ModerationMessageDto =
    ModerationMessageDto(
        id,
        evaluatorName,
        message,
        createdAt
    )
