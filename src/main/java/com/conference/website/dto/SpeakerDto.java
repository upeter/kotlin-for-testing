package com.conference.website.dto;

public record SpeakerDto(
        Long id,
        String name,
        String email,
        String company,
        String bio
) {
}
