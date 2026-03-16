package com.conference.website.data.builders;

import com.conference.website.dto.CreateSpeakerRequest;

public class CreateSpeakerRequestBuilder {

    private String name = "Ada Lovelace";
    private String email = "ada@example.com";
    private String company = "Analytical Engines";
    private String bio = "Pioneer in computing";

    public CreateSpeakerRequestBuilder() {
    }

    public static CreateSpeakerRequestBuilder aCreateSpeakerRequest() {
        return new CreateSpeakerRequestBuilder();
    }

    public static CreateSpeakerRequestBuilder from(CreateSpeakerRequest request) {
        return new CreateSpeakerRequestBuilder(request);
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

    public CreateSpeakerRequestBuilder(CreateSpeakerRequest request) {
        this.name = request.name();
        this.email = request.email();
        this.company = request.company();
        this.bio = request.bio();
    }

    public CreateSpeakerRequest build() {
        return new CreateSpeakerRequest(name, email, company, bio);
    }
}
