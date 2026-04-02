package com.conference.website.utils;

import com.conference.website.domain.Speaker;
import com.conference.website.domain.Talk;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TalkRepository;

import java.util.function.Function;

public final class E02_EntityLifecycleTestUtils {

    private E02_EntityLifecycleTestUtils() {
    }

    public static <T> T doWithSpeaker(
            SpeakerRepository speakerRepository,
            Speaker speaker,
            Function<Speaker, T> callback
    ) {
        Speaker savedSpeaker = speakerRepository.save(speaker);
        try {
            return callback.apply(savedSpeaker);
        }
        finally {
            //always ensure the speaker is deleted
            Long speakerId = savedSpeaker.getId();
            E02_TransactionTestUtils.doInCommittedTransaction(() -> {
                if (speakerId != null && speakerRepository.existsById(speakerId)) {
                    speakerRepository.deleteById(speakerId);
                }
            });
        }
    }

    public static <T> T doWithTalk(
            TalkRepository talkRepository,
            Talk talk,
            Function<Talk, T> callback
    ) {
        Talk savedTalk = talkRepository.save(talk);
        try {
            return callback.apply(savedTalk);
        }
        finally {
            E02_TransactionTestUtils.doInCommittedTransaction(() -> {
                Long talkId = savedTalk.getId();
                if (talkId != null && talkRepository.existsById(talkId)) {
                    talkRepository.deleteById(talkId);
                }
            });
        }
    }
}
