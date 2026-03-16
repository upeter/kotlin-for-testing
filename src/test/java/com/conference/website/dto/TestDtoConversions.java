package com.conference.website.dto;

import com.conference.website.domain.*;

import java.util.List;

public final class TestDtoConversions {

    public static SpeakerDto toDto(Long id, CreateSpeakerRequest request) {
        return new SpeakerDto(id, request.name(), request.email(), request.company(), request.bio());
    }

    public static SpeakerDto toDto(Speaker speaker) {
        return new SpeakerDto(speaker.getId(), speaker.getName(), speaker.getEmail(), speaker.getCompany(), speaker.getBio());
    }


    public static TagDto toDto(Tag tag) {
        return new TagDto(tag.getId(), tag.getName());
    }

    public static RatingDto toDto(Rating rating) {
        return new RatingDto(rating.getId(), rating.getReviewerName(), rating.getScore(), rating.getComment());
    }


    public static ScheduleSlotDto toDto(Long id, ScheduleSlotRequest request) {
        return new ScheduleSlotDto(id, request.roomName(), request.startTime(), request.endTime());
    }

    public static ScheduleSlotDto toDto(ScheduleSlot slot) {
        return new ScheduleSlotDto(slot.getId(), slot.getRoomName(), slot.getStartTime(), slot.getEndTime());
    }


    public static TalkDto toDto(Talk talk) {
        return new TalkDto(talk.getId(), talk.getTitle(), talk.getAbstractText(), talk.getLevel(), talk.getDurationMinutes(), toDto(talk.getPrimarySpeaker()), talk.getCoSpeakers().stream().map(TestDtoConversions::toDto).toList(), talk.getTags().stream().map(TestDtoConversions::toDto).toList(), talk.getRatings().stream().map(TestDtoConversions::toDto).toList(), toDto(talk.getScheduleSlot()), talk.getRatings().stream().mapToInt(i-> i.getScore()).average().getAsDouble() , Integer.valueOf(talk.getRatings().size()).longValue());
    }


    public static TalkDto toDto(Long id, SpeakerDto speaker, List<SpeakerDto>   coSpeakers,
                                List<TagDto> tagIds, List<RatingDto> ratings, ScheduleSlotDto scheduleSlotDto, CreateTalkRequest request) {
        return new TalkDto(id, request.title(), request.abstractText(), request.level(), request.durationMinutes(), speaker, coSpeakers, tagIds, ratings,scheduleSlotDto, ratings.stream().mapToInt(i-> i.score()).average().getAsDouble(),  ratings.stream().count());
    }

    public static TalkDto toDto(Long id, SpeakerDto speaker, ScheduleSlotDto scheduleSlotDto, CreateTalkRequest request) {
        return new TalkDto(id, request.title(), request.abstractText(), request.level(), request.durationMinutes(), speaker, List.of(), List.of(), List.of(),scheduleSlotDto, 0.0, 0L);
    }


    public static TalkDto toDto(Long id, CreateTalkRequest request) {
        return new TalkDto(id, request.title(), request.abstractText(), request.level(), request.durationMinutes(), request.primarySpeaker(), request.coSpeakers(), request.tags(), List.of(), null, 0.0, 0L);
    }
}
