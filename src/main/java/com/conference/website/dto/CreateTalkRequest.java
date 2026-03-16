package com.conference.website.dto;

import com.conference.website.domain.TalkLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateTalkRequest(
        @NotBlank String title,
        @NotBlank String abstractText,
        @NotNull TalkLevel level,
        @NotNull @Min(5) Integer durationMinutes,
        @Valid SpeakerDto primarySpeaker,
        List<@Valid SpeakerDto> coSpeakers,
        List<@Valid TagDto> tags
//        List<@Valid RatingDto> ratings,
//        @Valid ScheduleSlotDto scheduleSlot
) {
}
