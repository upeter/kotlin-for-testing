package com.conference.website.service

import com.conference.website.domain.Tag
import com.conference.website.dto.CreateTagsRequest
import com.conference.website.dto.TagDto
import com.conference.website.dto.toDto
import com.conference.website.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService(
    private val tagRepository: TagRepository
) {

    @Transactional
    fun createTags(request: CreateTagsRequest): List<TagDto> {
        val existingTags = tagRepository.findAllByNameLowerIn(request.names.map { it.lowercase() })

        if (existingTags.size == request.names.size) {
            throw BadRequestException("Tag already exists: ${existingTags.map { it.name }}")
        }
        return tagRepository.saveAll(request.names.map { Tag(it).apply { name = name.lowercase() } })
            .map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getAllTags(): List<TagDto> =
        tagRepository.findAll().map { it.toDto() }
}
