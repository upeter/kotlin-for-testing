package com.conference.website.service;

import com.conference.website.dto.CreateTagsRequest;
import com.conference.website.domain.Tag;
import com.conference.website.dto.DtoConversions;
import com.conference.website.dto.TagDto;
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
    public List<TagDto> createTags(CreateTagsRequest request) {
        var existingTags = tagRepository.findAllByNameLowerIn(request.names().stream().map(String::toLowerCase).toList());

        if (existingTags.size() == request.names().size()) {
            throw new BadRequestException("Tag already exists: " + existingTags.stream().map(Tag::getName).toList());
        }
        return tagRepository.saveAll(request.names().stream().map(Tag::new).peek(t -> t.setName(t.getName().toLowerCase())).toList())
                .stream().map(DtoConversions::toDto).toList();
    }

    @Transactional
    public List<TagDto> createTags(kom.conference.website.dto.CreateTagsRequest request) {
        var existingTags = tagRepository.findAllByNameLowerIn(request.names().stream().map(String::toLowerCase).toList());

        if (existingTags.size() == request.names().size()) {
            throw new BadRequestException("Tag already exists: " + existingTags.stream().map(Tag::getName).toList());
        }
        return tagRepository.saveAll(request.names().stream().map(Tag::new).peek(t -> t.setName(t.getName().toLowerCase())).toList())
                .stream().map(DtoConversions::toDto).toList();
    }


    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream().map(DtoConversions::toDto).toList();
    }
}
