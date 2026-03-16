package com.conference.website.data.builders;

import com.conference.website.dto.CreateSpeakerRequest;

public class CreateSpeakerRequestBuilder {

    private String name = "Ada Lovelace";
    private String email = "ada@example.com";
    private String company = "Analytical Engines";
    private String bio = "Pioneer in computing";

    public static CreateSpeakerRequestBuilder aCreateSpeakerRequest() {
        return new CreateSpeakerRequestBuilder();
    }

    public CreateSpeakerRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CreateSpeakerRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public CreateSpeakerRequestBuilder withCompany(String company) {
        this.company = company;
        return this;
    }

    public CreateSpeakerRequestBuilder withBio(String bio) {
        this.bio = bio;
        return this;
    }

    public CreateSpeakerRequest build() {
        return new CreateSpeakerRequest(name, email, company, bio);
    }
}
