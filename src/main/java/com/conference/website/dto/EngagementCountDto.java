package com.conference.website.dto;

public record EngagementCountDto(
        Long talkId,
        Long views,
        Long likes,
        Long attends
) {
}
