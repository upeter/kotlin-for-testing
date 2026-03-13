package com.conference.website.api.dto;

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
        @NotNull Long primarySpeakerId,
        List<Long> coSpeakerIds,
        List<Long> tagIds,
        @Valid ScheduleSlotRequest scheduleSlot
) {
}
