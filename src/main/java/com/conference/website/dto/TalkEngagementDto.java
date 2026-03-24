package com.conference.website.dto;

public record TalkEngagementDto(
        Long talkId,
        String title,
        Long views,
        Double buzzScore,
        Double engagementScore
) {
}
