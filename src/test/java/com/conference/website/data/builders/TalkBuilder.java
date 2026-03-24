package com.conference.website.data.builders;

import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TalkBuilder {

    private String title = "Kotlin for Java Developers";
    private String abstractText = "Learn Kotlin in 20 minutes";
    private TalkLevel level = TalkLevel.BEGINNER;
    private Integer durationMinutes = 20;
    private Speaker primarySpeaker;
    private Set<Speaker> coSpeakers = new LinkedHashSet<>();
    private Set<Tag> tags = new LinkedHashSet<>();

    public static TalkBuilder aTalk() {
        return new TalkBuilder();
    }

    public TalkBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TalkBuilder withAbstractText(String abstractText) {
        this.abstractText = abstractText;
        return this;
    }

    public TalkBuilder withLevel(TalkLevel level) {
        this.level = level;
        return this;
    }

    public TalkBuilder withDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
        return this;
    }

    public TalkBuilder withPrimarySpeaker(Speaker primarySpeaker) {
        this.primarySpeaker = primarySpeaker;
        return this;
    }

    public TalkBuilder withCoSpeaker(Speaker coSpeaker) {
        this.coSpeakers.add(coSpeaker);
        return this;
    }

    public TalkBuilder withCoSpeakers(List<Speaker> coSpeakers) {
        this.coSpeakers = new LinkedHashSet<>(coSpeakers);
        return this;
    }

    public TalkBuilder withTag(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public TalkBuilder withTags(List<Tag> tags) {
        this.tags = new LinkedHashSet<>(tags);
        return this;
    }

    public Talk build() {
        if (primarySpeaker == null) {
            throw new IllegalStateException("Primary speaker is required");
        }

        Talk talk = new Talk(title, abstractText, level, durationMinutes, primarySpeaker);
        talk.setCoSpeakers(coSpeakers);
        talk.setTags(tags);
        return talk;
    }
}
