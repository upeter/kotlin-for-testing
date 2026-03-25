package com.conference.website.repository

import com.conference.website.domain.Speaker
import com.conference.website.domain.Talk
import java.util.Locale

interface RepositorySupport {
    val speakerRepository: SpeakerRepository
    val tagRepository: TagRepository
    val talkRepository: TalkRepository

    fun Talk.persistGraph(): Talk = listOf(this).persistGraph().single()

    fun Talk.persistWithPostUndo(): Talk = talkRepository.save(this)
    fun List<Talk>.persistWithPostUndo(): List<Talk> = talkRepository.saveAll(this)
    fun Speaker.persistWithPostUndo(): Speaker = speakerRepository.save(this)
    //fun List<Speaker>.persist(): List<Speaker> = speakerRepository.saveAll(this)

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