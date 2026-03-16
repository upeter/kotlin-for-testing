package com.conference.website.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateSpeakerRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String company,
        @NotBlank String bio
) {
}
