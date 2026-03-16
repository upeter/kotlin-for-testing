package com.conference.website.data.builders;

import com.conference.website.dto.CreateTalkRequest;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.dto.TagDto;
import com.conference.website.domain.TalkLevel;

import java.util.List;

public class CreateTalkRequestBuilder {

    private String title = "Modern JVM testing";
    private String abstractText = "How to build robust and maintainable tests";
    private TalkLevel level = TalkLevel.INTERMEDIATE;
    private Integer durationMinutes = 45;
    private SpeakerDto primarySpeaker;
    private List<SpeakerDto> coSpeakers = List.of();
    private List<TagDto> tags = List.of();

    public static CreateTalkRequestBuilder aCreateTalkRequest() {
        return new CreateTalkRequestBuilder();
    }

    public CreateTalkRequestBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public CreateTalkRequestBuilder withAbstractText(String abstractText) {
        this.abstractText = abstractText;
        return this;
    }

    public CreateTalkRequestBuilder withLevel(TalkLevel level) {
        this.level = level;
        return this;
    }

    public CreateTalkRequestBuilder withDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        return this;
    }

    public CreateTalkRequestBuilder withPrimarySpeaker(SpeakerDto primarySpeaker) {
        this.primarySpeaker = primarySpeaker;
        return this;
    }

    public CreateTalkRequestBuilder withCoSpeakers(List<SpeakerDto> coSpeakers) {
        this.coSpeakers = coSpeakers;
        return this;
    }

    public CreateTalkRequestBuilder withTags(List<TagDto> tags) {
        this.tags = tags;
        return this;
    }

    public CreateTalkRequest build() {
        return new CreateTalkRequest(title, abstractText, level, durationMinutes, primarySpeaker, coSpeakers, tags);
    }
}
