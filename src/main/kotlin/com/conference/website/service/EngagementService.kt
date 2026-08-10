package com.conference.website.service

import com.conference.website.dto.EngagementCountDto
import com.conference.website.dto.EngagementUpdateRequest
import com.conference.website.integration.MetricsClient
import com.conference.website.repository.TalkRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class EngagementService(
    private val talkRepository: TalkRepository,
    private val metricsClient: MetricsClient
) {

    fun recordEngagement(
        talkId: Long,
        request: EngagementUpdateRequest
    ): Mono<EngagementCountDto> {
        ensureTalkExists(talkId)

        val recordViews: Mono<Void> =
            if (request.view) metricsClient.incrementViews(talkId).then() else Mono.empty()
        val recordLikes: Mono<Void> =
            if (request.like) metricsClient.incrementLikes(talkId).then() else Mono.empty()
        val recordAttends: Mono<Void> =
            if (request.attend) metricsClient.incrementAttends(talkId).then() else Mono.empty()

        return Mono.`when`(recordViews, recordLikes, recordAttends)
            .timeout(CLIENT_TIMEOUT)
            .then(getCurrentEngagement(talkId))
    }

    fun getCurrentEngagement(talkId: Long): Mono<EngagementCountDto> {
        ensureTalkExists(talkId)
        return Mono.zip(
            metricsClient.getViews(talkId),
            metricsClient.getLikes(talkId),
            metricsClient.getAttends(talkId)
        )
            .timeout(CLIENT_TIMEOUT)
            .map { tuple -> EngagementCountDto(talkId, tuple.t1, tuple.t2, tuple.t3) }
    }

    private fun ensureTalkExists(talkId: Long) {
        if (!talkRepository.existsById(talkId)) {
            throw NotFoundException("Talk not found: $talkId")
        }
    }

    companion object {
        private val CLIENT_TIMEOUT: Duration = Duration.ofSeconds(2)
    }
}
