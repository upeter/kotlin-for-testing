package com.conference.website.api.dto;

import java.time.Instant;

public record RatingResponse(
        Long id,
        String reviewerName,
        Integer score,
        String comment,
        Instant createdAt
) {
}
