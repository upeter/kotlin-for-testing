package com.conference.website.service

import com.conference.website.domain.Speaker
import com.conference.website.dto.CreateSpeakerRequest
import com.conference.website.dto.SpeakerDto
import com.conference.website.dto.toDto
import com.conference.website.repository.SpeakerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SpeakerService(
    private val speakerRepository: SpeakerRepository
) {


    @Transactional(readOnly = true)
    fun getAllSpeakers(): List<SpeakerDto> =
        speakerRepository.findAll().map { it.toDto() }

    @Transactional(readOnly = true)
    fun getSpeakerById(id: Long): SpeakerDto? =
        speakerRepository.findByIdOrNull(id)?.let { it.toDto() }

    @Transactional
    fun createSpeaker(request: CreateSpeakerRequest): SpeakerDto {
        speakerRepository.findByEmailIgnoreCase(request.email)?.let {
            throw BadRequestException("Speaker email already exists: ${request.email}")
        }

        val speaker = Speaker(
            request.name,
            request.email,
            request.company,
            request.bio
        )
        val saved = speakerRepository.save(speaker)
        return saved.toDto()
    }
}
