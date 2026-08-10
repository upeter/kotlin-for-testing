package com.conference.website.api

import com.conference.website.dto.CreateTagsRequest
import com.conference.website.dto.TagDto
import com.conference.website.service.TagService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tags")
class TagController(
    private val tagService: TagService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTags(@Valid @RequestBody request: CreateTagsRequest): List<TagDto> =
        tagService.createTags(request)

    @GetMapping
    fun listTags(): List<TagDto> =
        tagService.getAllTags()
}
