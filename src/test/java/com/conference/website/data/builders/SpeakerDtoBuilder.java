package com.conference.website.data.builders;

import com.conference.website.dto.SpeakerDto;

public class SpeakerDtoBuilder {

    private Long id = 1L;
    private String name = "Ada Lovelace";
    private String email = "ada@example.com";
    private String company = "Analytical Engines";
    private String bio = "Pioneer in computing";

    public static SpeakerDtoBuilder aSpeakerDto() {
        return new SpeakerDtoBuilder();
    }

    public SpeakerDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public SpeakerDtoBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SpeakerDtoBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public SpeakerDtoBuilder withCompany(String company) {
        this.company = company;
        return this;
    }

    public SpeakerDtoBuilder withBio(String bio) {
        this.bio = bio;
        return this;
    }

    public static SpeakerDtoBuilder from(SpeakerDto speaker) {
        var builder = new SpeakerDtoBuilder();
        builder.id = speaker.id();
        builder.name = speaker.name();
        builder.email = speaker.email();
        builder.company = speaker.company();
        builder.bio = speaker.bio();
        return builder;
    }

    public SpeakerDto build() {
        return new SpeakerDto(id, name, email, company, bio);
    }
}
