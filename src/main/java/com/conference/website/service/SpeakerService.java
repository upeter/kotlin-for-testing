package com.conference.website.service;

import com.conference.website.api.dto.CreateSpeakerRequest;
import com.conference.website.domain.Speaker;
import com.conference.website.repository.SpeakerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    @Transactional
    public Speaker createSpeaker(CreateSpeakerRequest request) {
        speakerRepository.findByEmailIgnoreCase(request.email())
                .ifPresent(existing -> {
                    throw new BadRequestException("Speaker email already exists: " + request.email());
                });

        Speaker speaker = new Speaker(
                request.name(),
                request.email(),
                request.company(),
                request.bio()
        );
        return speakerRepository.save(speaker);
    }

    @Transactional(readOnly = true)
    public List<Speaker> getAllSpeakers() {
        return speakerRepository.findAll();
    }
}
