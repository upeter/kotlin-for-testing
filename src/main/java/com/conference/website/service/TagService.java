package com.conference.website.service;

import com.conference.website.api.dto.CreateTagsRequest;
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
    public List<Tag> createTags(CreateTagsRequest request) {
        var existingTags = tagRepository.findAllByNameLowerIn(request.names().stream().map(String::toLowerCase).toList());

        if (existingTags.size() == request.names().size()) {
            throw new BadRequestException("Tag already exists: " + existingTags.stream().map(Tag::getName).toList());
        }
        return tagRepository.saveAll(request.names().stream().map(Tag::new).toList());
    }

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
