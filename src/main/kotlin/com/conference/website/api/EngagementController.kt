package com.conference.website.api

import com.conference.website.dto.*
import com.conference.website.service.EngagementService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping("/api/talks/stats")
class EngagementController(
    private val engagementService: EngagementService
) {

    @PostMapping("/{talkId}/engagement")
    fun recordEngagement(
        @PathVariable talkId: Long,
        @RequestBody request: EngagementUpdateRequest
    ): Mono<EngagementCountDto> =
        engagementService.recordEngagement(talkId, request)

    @GetMapping("/{talkId}/engagement")
    fun getEngagement(@PathVariable talkId: Long): Mono<EngagementCountDto> =
        engagementService.getCurrentEngagement(talkId)
}
