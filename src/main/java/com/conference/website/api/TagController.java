package com.conference.website.api;

import com.conference.website.api.dto.CreateTagRequest;
import com.conference.website.api.dto.TagDto;
import com.conference.website.service.TagService;
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
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(@Valid @RequestBody CreateTagRequest request) {
        return ConferenceApiMapper.toTagResponse(tagService.createTag(request));
    }

    @GetMapping
    public List<TagDto> listTags() {
        return tagService.getAllTags().stream()
                .map(ConferenceApiMapper::toTagResponse)
                .toList();
    }
}
