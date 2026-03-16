package com.conference.website.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateTagsRequest(
        @NotEmpty List<String> names
) {
}
