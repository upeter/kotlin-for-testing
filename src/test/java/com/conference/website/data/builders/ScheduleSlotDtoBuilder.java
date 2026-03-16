package com.conference.website.data.builders;

import com.conference.website.dto.ScheduleSlotDto;

import java.time.LocalDateTime;

public class ScheduleSlotDtoBuilder {

    private Long id = 1L;
    private String roomName = "Main Hall";
    private LocalDateTime startTime = LocalDateTime.of(2026, 4, 8, 10, 0);
    private LocalDateTime endTime = LocalDateTime.of(2026, 4, 8, 10, 45);

    public static ScheduleSlotDtoBuilder aScheduleSlotDto() {
        return new ScheduleSlotDtoBuilder();
    }

    public ScheduleSlotDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ScheduleSlotDtoBuilder withRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public ScheduleSlotDtoBuilder withStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public ScheduleSlotDtoBuilder withEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public ScheduleSlotDto build() {
        return new ScheduleSlotDto(id, roomName, startTime, endTime);
    }
}
