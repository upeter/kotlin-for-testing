package com.conference.website.service;

import com.conference.website.api.dto.CreateTagRequest;
import com.conference.website.domain.Tag;
import com.conference.website.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTag(CreateTagRequest request) {
        tagRepository.findByNameIgnoreCase(request.name())
                .ifPresent(existing -> {
                    throw new BadRequestException("Tag already exists: " + request.name());
                });

        Tag tag = new Tag(request.name());
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
