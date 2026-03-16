package com.conference.website.data.builders;

import com.conference.website.dto.CreateTagsRequest;

import java.util.List;

public class CreateTagRequestBuilder {

    private List<String> names = List.of("java");

    public static CreateTagRequestBuilder aCreateTagRequest() {
        return new CreateTagRequestBuilder();
    }

    public CreateTagRequestBuilder withName(String name) {
        this.names = names;
        return this;
    }

    public CreateTagsRequest build() {
        return new CreateTagsRequest(names);
    }
}
