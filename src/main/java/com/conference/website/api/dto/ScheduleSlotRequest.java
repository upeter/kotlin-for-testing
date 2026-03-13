package com.conference.website.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleSlotRequest(
        @NotBlank String roomName,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {
}
