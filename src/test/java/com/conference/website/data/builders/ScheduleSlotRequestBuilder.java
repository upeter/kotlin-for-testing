package com.conference.website.data.builders;

import com.conference.website.dto.ScheduleSlotRequest;

import java.time.LocalDateTime;

public class ScheduleSlotRequestBuilder {

    private String roomName = "Main Hall";
    private LocalDateTime startTime = LocalDateTime.of(2026, 4, 8, 10, 0);
    private LocalDateTime endTime = LocalDateTime.of(2026, 4, 8, 10, 45);

    public static ScheduleSlotRequestBuilder aScheduleSlotRequest() {
        return new ScheduleSlotRequestBuilder();
    }

    public ScheduleSlotRequestBuilder withRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public ScheduleSlotRequestBuilder withStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public ScheduleSlotRequestBuilder withEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public ScheduleSlotRequest build() {
        return new ScheduleSlotRequest(roomName, startTime, endTime);
    }
}
