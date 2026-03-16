package com.conference.website.data.builders;

import com.conference.website.dto.TagDto;

public class TagDtoBuilder {

    private Long id = 1L;
    private String name = "java";

    public static TagDtoBuilder aTagDto() {
        return new TagDtoBuilder();
    }

    public TagDtoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TagDtoBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TagDto build() {
        return new TagDto(id, name);
    }
}
