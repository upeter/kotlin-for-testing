package com.conference.website.api;

import com.conference.website.domain.TalkLevel;
import com.conference.website.dto.CreateRatingRequest;
import com.conference.website.dto.CreateTalkRequest;
import com.conference.website.dto.ScheduleSlotRequest;
import com.conference.website.dto.TalkDto;
import com.conference.website.service.TalkService;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/talks")
public class TalkController {

    private final TalkService talkService;

    public TalkController(TalkService talkService) {
        this.talkService = talkService;
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
