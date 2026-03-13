package com.conference.website.api;

import com.conference.website.api.dto.RatingResponse;
import com.conference.website.api.dto.ScheduleSlotResponse;
import com.conference.website.api.dto.SpeakerResponse;
import com.conference.website.api.dto.TagResponse;
import com.conference.website.api.dto.TalkResponse;
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

    public static SpeakerResponse toSpeakerResponse(Speaker speaker) {
        return new SpeakerResponse(
                speaker.getId(),
                speaker.getName(),
                speaker.getEmail(),
                speaker.getCompany(),
                speaker.getBio()
        );
    }

    public static TagResponse toTagResponse(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }

    public static @Nullable ScheduleSlotResponse toScheduleSlotResponse(@Nullable ScheduleSlot slot) {
        if (slot == null) {
            return null;
        }

        return new ScheduleSlotResponse(
                slot.getId(),
                slot.getRoomName(),
                slot.getStartTime(),
                slot.getEndTime()
        );
    }

    public static TalkResponse toTalkResponse(Talk talk) {
        List<RatingResponse> ratingResponses = talk.getRatings().stream()
                .sorted(Comparator.comparing(Rating::getCreatedAt).reversed())
                .map(ConferenceApiMapper::toRatingResponse)
                .toList();

        double averageRating = talk.getRatings().stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0d);

        return new TalkResponse(
                talk.getId(),
                talk.getTitle(),
                talk.getAbstractText(),
                talk.getLevel(),
                talk.getDurationMinutes(),
                talk.getCreatedAt(),
                toSpeakerResponse(talk.getPrimarySpeaker()),
                talk.getCoSpeakers().stream().map(ConferenceApiMapper::toSpeakerResponse).toList(),
                talk.getTags().stream().map(ConferenceApiMapper::toTagResponse).toList(),
                ratingResponses,
                toScheduleSlotResponse(talk.getScheduleSlot()),
                averageRating,
                (long) talk.getRatings().size()
        );
    }

    private static RatingResponse toRatingResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getReviewerName(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }
}
