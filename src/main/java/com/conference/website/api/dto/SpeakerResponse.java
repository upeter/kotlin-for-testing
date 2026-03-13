package com.conference.website.api.dto;

public record SpeakerResponse(
        Long id,
        String name,
        String email,
        String company,
        String bio
) {
}
