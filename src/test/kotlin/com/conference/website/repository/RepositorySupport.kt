package com.conference.website.repository

import com.conference.website.domain.Talk
import java.util.Locale

interface RepositorySupport {
    val speakerRepository: SpeakerRepository
    val tagRepository: TagRepository
    val talkRepository: TalkRepository

    fun Talk.persistGraph(): Talk = listOf(this).persistGraph().single()

    fun List<Talk>.persistGraph(): List<Talk> {
        val uniqueSpeakers = asSequence()
            .flatMap { sequenceOf(it.primarySpeaker) + it.coSpeakers.asSequence() }
            .groupBy { it.email.lowercase(Locale.ROOT) }
            .values
            .map { it.first() }

        val uniqueTags = asSequence()
            .flatMap { it.tags.asSequence() }
            .groupBy { it.name.lowercase(Locale.ROOT) }
            .values
            .map { it.first() }

        speakerRepository.saveAll(uniqueSpeakers)
        tagRepository.saveAll(uniqueTags)
        return talkRepository.saveAll(this)
    }
}