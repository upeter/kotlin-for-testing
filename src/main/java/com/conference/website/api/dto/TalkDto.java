package com.conference.website.api.dto;

import com.conference.website.domain.TalkLevel;

import java.time.Instant;
import java.util.List;

public record TalkDto(
        Long id,
        String title,
        String abstractText,
        TalkLevel level,
        Integer durationMinutes,
        Instant createdAt,
        SpeakerDto primarySpeaker,
        List<SpeakerDto> coSpeakers,
        List<TagDto> tags,
        List<RatingDto> ratings,
        ScheduleSlotDto scheduleSlot,
        Double averageRating,
        Long totalRatings
) {
}
