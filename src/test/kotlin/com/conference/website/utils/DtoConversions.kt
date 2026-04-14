package com.conference.website.utils

import com.conference.website.domain.Rating
import com.conference.website.domain.ScheduleSlot
import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag
import com.conference.website.domain.Talk
import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.RatingDto
import com.conference.website.dto.ScheduleSlotDto
import com.conference.website.dto.ScheduleSlotRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.TagDto
import com.conference.website.dto.TalkDto
import com.conference.website.dto.TestDtoConversions

fun CreateSpeakerRequest.toDto(id: Long): SpeakerDto = TestDtoConversions.toDto(id, this)

fun Speaker.toDto(): SpeakerDto = TestDtoConversions.toDto(this)

fun Tag.toDto(): TagDto = TestDtoConversions.toDto(this)

fun Rating.toDto(): RatingDto = TestDtoConversions.toDto(this)

fun ScheduleSlotRequest.toDto(id: Long): ScheduleSlotDto = TestDtoConversions.toDto(id, this)

fun ScheduleSlot.toDto(): ScheduleSlotDto = TestDtoConversions.toDto(this)

fun Talk.toDto(): TalkDto = TestDtoConversions.toDto(this)

fun CreateTalkRequest.toDto(
    id: Long,
    speaker: SpeakerDto,
    coSpeakers: List<SpeakerDto>,
    tagIds: List<TagDto>,
    ratings: List<RatingDto>,
    scheduleSlotDto: ScheduleSlotDto,
): TalkDto = TestDtoConversions.toDto(id, speaker, coSpeakers, tagIds, ratings, scheduleSlotDto, this)

fun CreateTalkRequest.toDto(
    id: Long,
    speaker: SpeakerDto,
    scheduleSlotDto: ScheduleSlotDto,
): TalkDto = TestDtoConversions.toDto(id, speaker, scheduleSlotDto, this)

fun CreateTalkRequest.toDto(id: Long): TalkDto = TestDtoConversions.toDto(id, this)

