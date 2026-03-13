package com.conference.website.service;

import com.conference.website.api.dto.CreateRatingRequest;
import com.conference.website.api.dto.CreateTalkRequest;
import com.conference.website.api.dto.ScheduleSlotRequest;
import com.conference.website.domain.Rating;
import com.conference.website.domain.ScheduleSlot;
import com.conference.website.domain.Speaker;
import com.conference.website.domain.Tag;
import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import com.conference.website.repository.SpeakerRepository;
import com.conference.website.repository.TagRepository;
import com.conference.website.repository.TalkRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
    public Talk createTalk(CreateTalkRequest request) {
        Speaker primarySpeaker = speakerRepository.findById(request.primarySpeakerId())
                .orElseThrow(() -> new NotFoundException("Primary speaker not found: " + request.primarySpeakerId()));

        Set<Speaker> coSpeakers = resolveCoSpeakers(request.coSpeakerIds(), request.primarySpeakerId());
        Set<Tag> tags = resolveTags(request.tagIds());

        Talk talk = new Talk(
                request.title(),
                request.abstractText(),
                request.level(),
                request.durationMinutes(),
                primarySpeaker
        );

        talk.setCoSpeakers(coSpeakers);
        talk.setTags(tags);
        talk.setScheduleSlot(toScheduleSlot(request.scheduleSlot()));

        return talkRepository.save(talk);
    }

    @Transactional(readOnly = true)
    public List<Talk> listTalks(@Nullable TalkLevel level, @Nullable String tag) {
        if (level != null) {
            return talkRepository.findByLevel(level);
        }
        if (tag != null && !tag.isBlank()) {
            return talkRepository.findByTagsNameIgnoreCase(tag);
        }
        return talkRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Talk getTalk(Long talkId) {
        return talkRepository.findDetailedById(talkId)
                .orElseThrow(() -> new NotFoundException("Talk not found: " + talkId));
    }

    @Transactional
    public Talk addRating(Long talkId, CreateRatingRequest request) {
        Talk talk = talkRepository.findDetailedById(talkId)
                .orElseThrow(() -> new NotFoundException("Talk not found: " + talkId));

        Rating rating = new Rating(request.reviewerName(), request.score(), request.comment());
        talk.addRating(rating);
        return talkRepository.save(talk);
    }

    @Transactional
    public Talk assignSchedule(Long talkId, ScheduleSlotRequest request) {
        Talk talk = talkRepository.findDetailedById(talkId)
                .orElseThrow(() -> new NotFoundException("Talk not found: " + talkId));

        talk.setScheduleSlot(toScheduleSlot(request));
        return talkRepository.save(talk);
    }

    private Set<Speaker> resolveCoSpeakers(List<Long> coSpeakerIds, Long primarySpeakerId) {
        if (coSpeakerIds == null || coSpeakerIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Long> requestedIds = new ArrayList<>(new LinkedHashSet<>(coSpeakerIds));
        if (requestedIds.contains(primarySpeakerId)) {
            throw new BadRequestException("Primary speaker cannot also be a co-speaker");
        }

        List<Speaker> speakers = speakerRepository.findAllById(requestedIds);
        if (speakers.size() != requestedIds.size()) {
            throw new BadRequestException("One or more co-speaker ids are invalid");
        }
        return new LinkedHashSet<>(speakers);
    }

    private Set<Tag> resolveTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        List<Long> requestedIds = new ArrayList<>(new LinkedHashSet<>(tagIds));
        List<Tag> tags = tagRepository.findAllById(requestedIds);
        if (tags.size() != requestedIds.size()) {
            throw new BadRequestException("One or more tag ids are invalid");
        }
        return new LinkedHashSet<>(tags);
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
