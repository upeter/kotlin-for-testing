package com.conference.website.api.dto;

public record SpeakerDto(
        Long id,
        String name,
        String email,
        String company,
        String bio
) {
}
