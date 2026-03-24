package com.conference.website.data.builders;

import com.conference.website.domain.Tag;

public class TagBuilder {

    private String name = "java";

    public static TagBuilder aTag() {
        return new TagBuilder();
    }

    public static TagBuilder from(Tag tag) {
        return new TagBuilder().withName(tag.getName());
    }

    public TagBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public Tag build() {
        return new Tag(name);
    }
}
