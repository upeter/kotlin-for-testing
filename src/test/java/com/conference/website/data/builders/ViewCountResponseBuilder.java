package com.conference.website.data.builders;

import com.conference.website.dto.ViewCountDto;

public class ViewCountResponseBuilder {

    private Long talkId = 1L;
    private Long views = 0L;

    public static ViewCountResponseBuilder aViewCountResponse() {
        return new ViewCountResponseBuilder();
    }

    public ViewCountResponseBuilder withTalkId(Long talkId) {
        this.talkId = talkId;
        return this;
    }

    public ViewCountResponseBuilder withViews(Long views) {
        this.views = views;
        return this;
    }

    public ViewCountDto build() {
        return new ViewCountDto(talkId, views);
    }
}
