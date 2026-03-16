package com.conference.website.dto;

import java.time.Instant;

public record RatingDto(
        Long id,
        String reviewerName,
        Integer score,
        String comment
//        Instant createdAt
) {
}
