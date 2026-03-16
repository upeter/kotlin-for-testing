package com.conference.website.data.builders;

import com.conference.website.dto.RatingDto;
import com.conference.website.dto.ScheduleSlotDto;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.dto.TagDto;
import com.conference.website.dto.TalkDto;
import com.conference.website.domain.TalkLevel;

import java.util.List;

public class TalkDtoBuilder {

    private Long id = 1L;
    private String title = "Modern JVM testing";
    private String abstractText = "How to build robust and maintainable tests";
    private TalkLevel level = TalkLevel.INTERMEDIATE;
    private Integer durationMinutes = 45;
    private SpeakerDto primarySpeaker = SpeakerDtoBuilder.aSpeakerDto().build();
    private List<SpeakerDto> coSpeakers = List.of(
            SpeakerDtoBuilder.aSpeakerDto()
                    .withId(2L)
                    .withName("Grace Hopper")
                    .withEmail("grace@example.com")
                    .withCompany("US Navy")
                    .withBio("Invented modern compiler foundations")
                    .build()
    );
    private List<TagDto> tags = List.of(TagDtoBuilder.aTagDto().build());
    private List<RatingDto> ratings = List.of(RatingDtoBuilder.aRatingDto().build());
    private ScheduleSlotDto scheduleSlot = ScheduleSlotDtoBuilder.aScheduleSlotDto().build();
    private Double averageRating = 5.0d;
    private Long totalRatings = 1L;

    public static TalkDtoBuilder aTalkDto() {
        return new TalkDtoBuilder();
    }

    public TalkDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TalkDtoBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TalkDtoBuilder withAbstractText(String abstractText) {
        this.abstractText = abstractText;
        return this;
    }

    public TalkDtoBuilder withLevel(TalkLevel level) {
        this.level = level;
        return this;
    }

    public TalkDtoBuilder withDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        return this;
    }

    public TalkDtoBuilder withPrimarySpeaker(SpeakerDto primarySpeaker) {
        this.primarySpeaker = primarySpeaker;
        return this;
    }

    public TalkDtoBuilder withCoSpeakers(List<SpeakerDto> coSpeakers) {
        this.coSpeakers = coSpeakers;
        return this;
    }

    public TalkDtoBuilder withTags(List<TagDto> tags) {
        this.tags = tags;
        return this;
    }

    public TalkDtoBuilder withRatings(List<RatingDto> ratings) {
        this.ratings = ratings;
        return this;
    }

    public TalkDtoBuilder withScheduleSlot(ScheduleSlotDto scheduleSlot) {
        this.scheduleSlot = scheduleSlot;
        return this;
    }

    public TalkDtoBuilder withAverageRating(Double averageRating) {
        this.averageRating = averageRating;
        return this;
    }

    public TalkDtoBuilder withTotalRatings(Long totalRatings) {
        this.totalRatings = totalRatings;
        return this;
    }

    public TalkDto build() {
        return new TalkDto(
                id,
                title,
                abstractText,
                level,
                durationMinutes,
                primarySpeaker,
                coSpeakers,
                tags,
                ratings,
                scheduleSlot,
                averageRating,
                totalRatings
        );
    }
}
