package com.conference.website.api;

import com.conference.website.api.dto.RatingDto;
import com.conference.website.api.dto.ScheduleSlotDto;
import com.conference.website.api.dto.SpeakerDto;
import com.conference.website.api.dto.TagDto;
import com.conference.website.api.dto.TalkDto;
import com.conference.website.domain.Rating;
import com.conference.website.domain.ScheduleSlot;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public final class ConferenceApiMapper {

    private ConferenceApiMapper() {
    }

    public static SpeakerDto toSpeakerResponse(Speaker speaker) {
        return new SpeakerDto(
                speaker.getId(),
                speaker.getName(),
                speaker.getEmail(),
                speaker.getCompany(),
                speaker.getBio()
        );
    }

    public static TagDto toTagResponse(Tag tag) {
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

    public static TalkDto toTalkResponse(Talk talk) {
        List<RatingDto> ratingDto = talk.getRatings().stream()
                .sorted(Comparator.comparing(Rating::getCreatedAt).reversed())
                .map(ConferenceApiMapper::toRatingResponse)
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
                talk.getCreatedAt(),
                toSpeakerResponse(talk.getPrimarySpeaker()),
                talk.getCoSpeakers().stream().map(ConferenceApiMapper::toSpeakerResponse).toList(),
                talk.getTags().stream().map(ConferenceApiMapper::toTagResponse).toList(),
                ratingDto,
                toScheduleSlotResponse(talk.getScheduleSlot()),
                averageRating,
                (long) talk.getRatings().size()
        );
    }

    private static RatingDto toRatingResponse(Rating rating) {
        return new RatingDto(
                rating.getId(),
                rating.getReviewerName(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }
}
