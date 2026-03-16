package com.conference.website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleSlotDto(
         Long id,
         @NotBlank String roomName,
         @NotNull LocalDateTime startTime,
         @NotNull LocalDateTime endTime
) {
}
