package com.conference.website.api.dto;

import com.conference.website.domain.*;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public final class DtoConversions {

    private DtoConversions() {
    }

    public static SpeakerDto toDto(@NotNull Speaker speaker) {
        return new SpeakerDto(
                speaker.getId(),
                speaker.getName(),
                speaker.getEmail(),
                speaker.getCompany(),
                speaker.getBio()
                );
    }

    public static TagDto toDto(Tag tag) {
        return new TagDto(tag.getId(), tag.getName());
    }

    public static @Nullable ScheduleSlotDto toScheduleSlotResponse(@Nullable ScheduleSlot slot) {
        if (slot == null) {
            return null;
        }

        return new ScheduleSlotDto(
                slot.getId(),
                slot.getRoomName(),
                slot.getStartTime(),
                slot.getEndTime()
        );
    }

    public static TalkDto toDto(Talk talk) {
        List<RatingDto> ratingDto = talk.getRatings().stream()
                .sorted(Comparator.comparing(Rating::getCreatedAt).reversed())
                .map(DtoConversions::toDto)
                .toList();

        double averageRating = talk.getRatings().stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0d);

        return new TalkDto(
                talk.getId(),
                talk.getTitle(),
                talk.getAbstractText(),
                talk.getLevel(),
                talk.getDurationMinutes(),
//                talk.getCreatedAt(),
                toDto(talk.getPrimarySpeaker()),
                talk.getCoSpeakers().stream().map(DtoConversions::toDto).toList(),
                talk.getTags().stream().map(DtoConversions::toDto).toList(),
                ratingDto,
                toScheduleSlotResponse(talk.getScheduleSlot()),
                averageRating,
                (long) talk.getRatings().size()
        );
    }

    private static RatingDto toDto(Rating rating) {
        return new RatingDto(
                rating.getId(),
                rating.getReviewerName(),
                rating.getScore(),
                rating.getComment()
                //rating.getCreatedAt()
        );
    }

}
