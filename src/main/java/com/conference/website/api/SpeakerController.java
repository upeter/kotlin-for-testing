package com.conference.website.api;

import com.conference.website.dto.CreateSpeakerRequest;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.service.SpeakerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpeakerDto createSpeaker(@Valid @RequestBody CreateSpeakerRequest request) {
        return speakerService.createSpeaker(request);
    }

    @GetMapping
    public List<SpeakerDto> listSpeakers() {
        return speakerService.getAllSpeakers();
    }
}
