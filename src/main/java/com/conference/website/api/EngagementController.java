package com.conference.website.api;

import com.conference.website.dto.*;
import com.conference.website.service.EngagementService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/api/talks/stats")
public class EngagementController {

    private final EngagementService engagementService;

    public EngagementController(
                               EngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @PostMapping("/{talkId}/engagement")
    public Mono<EngagementCountDto> recordEngagement(@PathVariable Long talkId, @RequestBody EngagementUpdateRequest request) {
        return engagementService.recordEngagement(talkId, request);
    }

    @GetMapping("/{talkId}/engagement")
    public Mono<EngagementCountDto> getEngagement(@PathVariable Long talkId) {
        return engagementService.getCurrentEngagement(talkId);
    }

}
