package com.conference.website.dto;

public record EngagementUpdateRequest(
        boolean view,
        boolean like,
        boolean attend
) {
}
