package com.conference.website.api.dto;

public record ViewCountResponse(
        Long talkId,
        Long views
) {
}
