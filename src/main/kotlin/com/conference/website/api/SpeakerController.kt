package com.conference.website.api

import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.service.SpeakerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/speakers")
class SpeakerController(
    private val speakerService: SpeakerService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSpeaker(@Valid @RequestBody request: CreateSpeakerRequest): SpeakerDto =
        speakerService.createSpeaker(request)

    @GetMapping
    fun listSpeakers(): List<SpeakerDto> =
        speakerService.getAllSpeakers()
}
