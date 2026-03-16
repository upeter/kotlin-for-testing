package com.conference.website.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRatingRequest(
        @NotBlank String reviewerName,
        @NotNull @Min(1) @Max(5) Integer score,
        @NotBlank String comment
) {
}
