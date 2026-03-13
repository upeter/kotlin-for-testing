package com.conference.website.api.dto;

import com.conference.website.domain.TalkLevel;

import java.time.Instant;
import java.util.List;

public record TalkResponse(
        Long id,
        String title,
        String abstractText,
        TalkLevel level,
        Integer durationMinutes,
        Instant createdAt,
        SpeakerResponse primarySpeaker,
        List<SpeakerResponse> coSpeakers,
        List<TagResponse> tags,
        List<RatingResponse> ratings,
        ScheduleSlotResponse scheduleSlot,
        Double averageRating,
        Long totalRatings
) {
}
