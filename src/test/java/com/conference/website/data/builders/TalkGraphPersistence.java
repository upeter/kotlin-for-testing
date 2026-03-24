package com.conference.website.data.builders;

import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TagRepository;
import com.conference.website.repository.TalkRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TalkGraphPersistence {

    private TalkGraphPersistence() {
    }

    public static Talk persistGraph(
            Talk talk,
            SpeakerRepository speakerRepository,
            TagRepository tagRepository,
            TalkRepository talkRepository
    ) {
        return persistGraph(List.of(talk), speakerRepository, tagRepository, talkRepository).getFirst();
    }

    public static List<Talk> persistGraph(
            List<Talk> talks,
            SpeakerRepository speakerRepository,
            TagRepository tagRepository,
            TalkRepository talkRepository
    ) {
        List<Speaker> uniqueSpeakers = talks.stream()
                .flatMap(talk -> Stream.concat(Stream.of(talk.getPrimarySpeaker()), talk.getCoSpeakers().stream()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                speaker -> speaker.getEmail().toLowerCase(Locale.ROOT),
                                Function.identity(),
                                (first, ignored) -> first,
                                LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        List<Tag> uniqueTags = talks.stream()
                .flatMap(talk -> talk.getTags().stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                tag -> tag.getName().toLowerCase(Locale.ROOT),
                                Function.identity(),
                                (first, ignored) -> first,
                                LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        speakerRepository.saveAll(uniqueSpeakers);
        tagRepository.saveAll(uniqueTags);
        return talkRepository.saveAll(talks);
    }
}
