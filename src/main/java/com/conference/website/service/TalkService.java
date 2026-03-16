package com.conference.website.service;

import com.conference.website.domain.Rating;
import com.conference.website.domain.ScheduleSlot;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.*;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TagRepository;
import com.conference.website.repository.TalkRepository;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class TalkService {

    private final TalkRepository talkRepository;
    private final SpeakerRepository speakerRepository;
    private final TagRepository tagRepository;

    public TalkService(TalkRepository talkRepository, SpeakerRepository speakerRepository, TagRepository tagRepository) {
        this.talkRepository = talkRepository;
        this.speakerRepository = speakerRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public TalkDto createTalk(CreateTalkRequest request) {
        Speaker primarySpeaker = speakerRepository.findById(request.primarySpeaker().id())
                .orElseThrow(() -> new NotFoundException("Primary speaker not found: " + request.primarySpeaker()));

        Set<Speaker> coSpeakers = resolveCoSpeakers(request.coSpeakers(), request.primarySpeaker());
        Set<Tag> tags = resolveTags(request.tags());

        Talk talk = new Talk(
                request.title(),
                request.abstractText(),
                request.level(),
                request.durationMinutes(),
                primarySpeaker
        );

        talk.setCoSpeakers(coSpeakers);
        talk.setTags(tags);

        return DtoConversions.toDto(talkRepository.save(talk));
    }

    @Transactional(readOnly = true)
    public List<TalkDto> listTalks(@Nullable TalkLevel level, @Nullable String tag) {
        if (level != null) {
            return talkRepository.findByLevel(level).stream().map(DtoConversions::toDto).toList();
        }
        if (tag != null && !tag.isBlank()) {
            return talkRepository.findByTagsNameIgnoreCase(tag).stream().map(DtoConversions::toDto).toList();
        }
        return talkRepository.findAllByOrderByCreatedAtDesc().stream().map(DtoConversions::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TalkDto> listTalks() {
        return talkRepository.findAllByOrderByCreatedAtDesc().stream().map(DtoConversions::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TalkDto getTalk(Long talkId) {
        return talkRepository.findDetailedById(talkId).map(DtoConversions::toDto)
                .orElseThrow(() -> new NotFoundException("Talk not found: " + talkId));
    }

    @Transactional
    public TalkDto addRating(Long talkId, CreateRatingRequest request) {
        Talk talk = talkRepository.findDetailedById(talkId)
                .orElseThrow(() -> new NotFoundException("Talk not found: " + talkId));

        Rating rating = new Rating(request.reviewerName(), request.score(), request.comment());
        talk.addRating(rating);
        return DtoConversions.toDto(talkRepository.save(talk));
    }

    @Transactional
    public TalkDto assignSchedule(Long talkId, ScheduleSlotRequest request) {
        Talk talk = talkRepository.findDetailedById(talkId)
                .orElseThrow(() -> new NotFoundException("Talk not found: " + talkId));

        talk.setScheduleSlot(toScheduleSlot(request));
        return DtoConversions.toDto(talkRepository.save(talk));
    }


    private Set<Speaker> resolveCoSpeakers(List<SpeakerDto> coSpeakerDtos, @NotNull SpeakerDto primarySpeakerDto) {
        if (coSpeakerDtos == null || coSpeakerDtos.isEmpty()) {
            return new LinkedHashSet<>();
        }
        var primarySpeaker = speakerRepository.findById(primarySpeakerDto.id()).orElseThrow(() -> new NotFoundException("Primary speaker not found: " + primarySpeakerDto.id()));

        List<Speaker> coSpeakers = speakerRepository.findAllById(coSpeakerDtos.stream().map(SpeakerDto::id).toList());
        if (coSpeakers.size() != coSpeakerDtos.size()) {
            throw new BadRequestException("One or more co-speaker are invalid");
        }
        if (coSpeakers.stream().map(Speaker::getId).toList().contains(primarySpeaker.getId())) {
            throw new BadRequestException("Primary speaker cannot also be a co-speaker");
        }

        return new LinkedHashSet<>(coSpeakers);
    }

    private Set<Tag> resolveTags(List<TagDto> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Tag> tags = tagRepository.findAllById(tagNames.stream().map(TagDto::id).toList());
        if (tagNames.size() != tags.size()) {
            throw new BadRequestException("One or more tag names are invalid");
        }
        return new LinkedHashSet<>(tags);
    }

    private List<String> normalizeValues(List<String> values, String fieldName) {
        List<String> normalized = values.stream().map(TalkService::normalizeValue).toList();
        if (normalized.stream().anyMatch(String::isBlank)) {
            throw new BadRequestException("One or more " + fieldName + " are blank");
        }
        return new ArrayList<>(new LinkedHashSet<>(normalized));
    }

    private static String normalizeValue(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private @Nullable ScheduleSlot toScheduleSlot(@Nullable ScheduleSlotRequest request) {
        if (request == null) {
            return null;
        }

        if (!request.endTime().isAfter(request.startTime())) {
            throw new BadRequestException("Schedule endTime must be after startTime");
        }

        return new ScheduleSlot(request.roomName(), request.startTime(), request.endTime());
    }
}
