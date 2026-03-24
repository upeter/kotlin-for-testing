package com.conference.website.data.builders;

import com.conference.website.domain.Speaker;

public class SpeakerBuilder {

    private String name = "Ada Lovelace";
    private String email = "ada@example.com";
    private String company = "Analytical Engines";
    private String bio = "Pioneer in computing";

    public static SpeakerBuilder aSpeaker() {
        return new SpeakerBuilder();
    }

    public static SpeakerBuilder from(Speaker speaker) {
        return new SpeakerBuilder()
                .withName(speaker.getName())
                .withEmail(speaker.getEmail())
                .withCompany(speaker.getCompany())
                .withBio(speaker.getBio());
    }

    public SpeakerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SpeakerBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public SpeakerBuilder withCompany(String company) {
        this.company = company;
        return this;
    }

    public SpeakerBuilder withBio(String bio) {
        this.bio = bio;
        return this;
    }

    public Speaker build() {
        return new Speaker(name, email, company, bio);
    }
}
