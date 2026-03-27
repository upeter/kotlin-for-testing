package com.conference.website.api;

import com.conference.website.dto.*;
import com.conference.website.service.ViewTrackingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/api/talks/stats")
public class ViewStatsController {

    private final ViewTrackingService viewTrackingService;

    public ViewStatsController(
                               ViewTrackingService viewTrackingService) {
        this.viewTrackingService = viewTrackingService;
    }

    @PostMapping("/{talkId}/views")
    public Mono<ViewCountDto> recordView(@PathVariable Long talkId) {
        return viewTrackingService.recordView(talkId)
                .map(views -> new ViewCountDto(talkId, views));
    }

    @GetMapping("/{talkId}/views")
    public Mono<ViewCountDto> getViews(@PathVariable Long talkId) {
        return viewTrackingService.getCurrentViews(talkId)
                .map(views -> new ViewCountDto(talkId, views));
    }

    @PostMapping("/{talkId}/engagement")
    public Mono<EngagementCountDto> recordEngagement(@PathVariable Long talkId, @RequestBody EngagementUpdateRequest request) {
        return viewTrackingService.recordEngagement(talkId, request);
    }

    @GetMapping("/{talkId}/engagement")
    public Mono<EngagementCountDto> getEngagement(@PathVariable Long talkId) {
        return viewTrackingService.getCurrentEngagement(talkId);
    }

}
