package com.conference.website.api;

import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.*;
import com.conference.website.service.EngagementService;
import com.conference.website.service.TalkService;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/talks")
public class TalkController {

    private final TalkService talkService;
    private final EngagementService engagementService;

    public TalkController(TalkService talkService,
                          EngagementService engagementService) {
        this.talkService = talkService;
        this.engagementService = engagementService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TalkDto createTalk(@Valid @RequestBody CreateTalkRequest request) {
        return talkService.createTalk(request);
    }

    @GetMapping
    public List<TalkDto> listTalks(@RequestParam(required = false) @Nullable TalkLevel level,
                                   @RequestParam(required = false) @Nullable String tag) {
        return talkService.listTalks(level, tag);
    }

    @GetMapping("/{talkId}")
    public TalkDto getTalk(@PathVariable Long talkId) {
        return talkService.getTalk(talkId);
    }

    @PostMapping("/{talkId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public TalkDto addRating(@PathVariable Long talkId, @Valid @RequestBody CreateRatingRequest request) {
        return talkService.addRating(talkId, request);
    }

    @PutMapping("/{talkId}/schedule")
    public TalkDto assignSchedule(@PathVariable Long talkId, @Valid @RequestBody ScheduleSlotRequest request) {
        return talkService.assignSchedule(talkId, request);
    }



}
