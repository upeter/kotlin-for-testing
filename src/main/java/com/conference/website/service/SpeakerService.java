package com.conference.website.service;

import com.conference.website.dto.CreateSpeakerRequest;
import com.conference.website.dto.DtoConversions;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.domain.Speaker;
import com.conference.website.repository.SpeakerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpeakerService {

    private final SpeakerRepository speakerRepository;

    public SpeakerService(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
    }

    @Transactional
    public SpeakerDto createSpeaker(CreateSpeakerRequest request) {
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
        var saved =  speakerRepository.save(speaker);
        return DtoConversions.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SpeakerDto> getAllSpeakers() {
        return speakerRepository.findAll().stream().map(DtoConversions::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<SpeakerDto> getSpeakerById(Long id) {
        return speakerRepository.findById(id).map(DtoConversions::toDto);
    }
}
