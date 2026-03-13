package com.conference.website.api.dto;

import java.time.LocalDateTime;

public record ScheduleSlotResponse(
        Long id,
        String roomName,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
