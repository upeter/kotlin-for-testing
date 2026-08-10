package com.conference.website.api

import com.conference.website.domain.TalkLevel
import com.conference.website.dto.CreateModerationMessageRequest
import com.conference.website.dto.CreateRatingRequest
import com.conference.website.dto.CreateTalkRequest
import com.conference.website.dto.ScheduleSlotRequest
import com.conference.website.dto.TalkDto
import com.conference.website.dto.UpdateEvaluationStatusRequest
import com.conference.website.service.TalkService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/talks")
class TalkController(
    private val talkService: TalkService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTalk(@Valid @RequestBody request: CreateTalkRequest): TalkDto =
        talkService.createTalk(request)

    @GetMapping
    fun listTalks(
        @RequestParam(required = false) level: TalkLevel?,
        @RequestParam(required = false) tag: String?
    ): List<TalkDto> = talkService.listTalks(level, tag)

    @GetMapping("/{talkId}")
    fun getTalk(@PathVariable talkId: Long): TalkDto =
        talkService.getTalk(talkId)

    @PostMapping("/{talkId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    fun addRating(@PathVariable talkId: Long, @Valid @RequestBody request: CreateRatingRequest): TalkDto =
        talkService.addRating(talkId, request)

    @PutMapping("/{talkId}/schedule")
    fun assignSchedule(@PathVariable talkId: Long, @Valid @RequestBody request: ScheduleSlotRequest): TalkDto =
        talkService.assignSchedule(talkId, request)

    @PutMapping("/{talkId}/evaluation-status")
    fun updateEvaluationStatus(
        @PathVariable talkId: Long,
        @Valid @RequestBody request: UpdateEvaluationStatusRequest
    ): TalkDto = talkService.updateEvaluationStatus(talkId, request)

    @PostMapping("/{talkId}/moderation-messages")
    @ResponseStatus(HttpStatus.CREATED)
    fun addModerationMessage(
        @PathVariable talkId: Long,
        @Valid @RequestBody request: CreateModerationMessageRequest
    ): TalkDto = talkService.addModerationMessage(talkId, request)
}
