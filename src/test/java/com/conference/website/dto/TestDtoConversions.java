package com.conference.website.dto;

import com.conference.website.api.dto.*;

import java.time.Instant;
import java.util.List;

final public class TestDtoConversions {

    public static SpeakerDto toDto(Long id, CreateSpeakerRequest request) {
        return new SpeakerDto(id, request.name(), request.email(), request.company(), request.bio());
    }

    public static ScheduleSlotDto toDto(Long id, ScheduleSlotRequest request) {
        return new ScheduleSlotDto(id, request.roomName(), request.startTime(), request.endTime());
    }


    public static TalkDto toDto(Long id, SpeakerDto speaker, List<SpeakerDto>   coSpeakers,
                                List<TagDto> tagIds, List<RatingDto> ratings, ScheduleSlotDto scheduleSlotDto, CreateTalkRequest request) {
        return new TalkDto(id, request.title(), request.abstractText(), request.level(), request.durationMinutes(), speaker, coSpeakers, tagIds, ratings,scheduleSlotDto, ratings.stream().mapToInt(i-> i.score()).average().getAsDouble(),  ratings.stream().count());
    }

    public static TalkDto toDto(Long id, SpeakerDto speaker, ScheduleSlotDto scheduleSlotDto, CreateTalkRequest request) {
        return new TalkDto(id, request.title(), request.abstractText(), request.level(), request.durationMinutes(), speaker, List.of(), List.of(), List.of(),scheduleSlotDto, 0.0, 0L);
    }


}
