package com.conference.website.utils

import com.conference.website.domain.Speaker
import com.conference.website.domain.Talk
import com.conference.website.repository.SpeakerRepository
import com.conference.website.repository.TalkRepository

/**
 * The inferior entity-lifecycle helper: one hand-written function per entity type,
 * each taking its repository as an explicit parameter. Adding a third entity means
 * adding a third near-identical function, and nesting two of them means nesting two
 * lambdas.
 *
 * Compare with `repository/RepositorySupport.kt` + `dsl/E02_UndoDataScopeDsl.kt`,
 * where a single `persistWithUndo()` covers every type and the cleanup registers
 * itself flatly instead of pyramiding.
 */
object E02_EntityLifecycleTestUtils {

    fun <T> doWithSpeaker(
        speakerRepository: SpeakerRepository,
        speaker: Speaker,
        callback: (Speaker) -> T
    ): T {
        val savedSpeaker = speakerRepository.save(speaker)
        try {
            return callback(savedSpeaker)
        } finally {
            // always ensure the speaker is deleted
            val speakerId = savedSpeaker.id
            E02_TransactionTestUtils.doInCommittedTransaction {
                if (speakerId != null && speakerRepository.existsById(speakerId)) {
                    speakerRepository.deleteById(speakerId)
                }
            }
        }
    }

    fun <T> doWithTalk(
        talkRepository: TalkRepository,
        talk: Talk,
        callback: (Talk) -> T
    ): T {
        val savedTalk = talkRepository.save(talk)
        try {
            return callback(savedTalk)
        } finally {
            E02_TransactionTestUtils.doInCommittedTransaction {
                val talkId = savedTalk.id
                if (talkId != null && talkRepository.existsById(talkId)) {
                    talkRepository.deleteById(talkId)
                }
            }
        }
    }
}
