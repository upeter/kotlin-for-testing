package com.conference.website.repository

import com.conference.website.domain.Speaker
import com.conference.website.domain.Talk
import com.conference.website.dsl.TestDataScope
import java.util.Locale

interface RepositorySupport {
    val speakerRepository: SpeakerRepository
    val tagRepository: TagRepository
    val talkRepository: TalkRepository

    context(scope:TestDataScope)
    fun Talk.persistWithUndo(): Talk =  with(scope){
        talkRepository.persistWithUndo(this@persistWithUndo).first()
    }

    context(scope:TestDataScope)
    fun List<Talk>.persistWithUndo(): List<Talk> = with(scope){
        talkRepository.persistWithUndo(this@persistWithUndo)
    }

    context(scope:TestDataScope)
    fun Speaker.persistWithUndo(): Speaker =  with(scope){
        speakerRepository.persistWithUndo(this@persistWithUndo).first()
    }

    fun List<Talk>.persistGraph(): List<Talk> {
        val uniqueSpeakers = flatMap { listOf(it.primarySpeaker) + it.coSpeakers }
            .groupBy { it.email.lowercase(Locale.ROOT) }
            .values
            .map { it.first() }

        val uniqueTags = flatMap { it.tags }
            .groupBy { it.name.lowercase(Locale.ROOT) }
            .values
            .map { it.first() }

        speakerRepository.saveAll(uniqueSpeakers)
        tagRepository.saveAll(uniqueTags)
        return talkRepository.saveAll(this).also { talkRepository.flush() }
    }
}