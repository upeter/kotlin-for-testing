package com.conference.website.service

import com.conference.website.domain.TalkLevel
import com.conference.website.dto.TalkDto
import com.conference.website.integration.BuzzClient
import com.conference.website.integration.MetricsClient
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import reactor.core.publisher.Mono

class TalkEngagementServiceSuperchargedTest {

    private val talkService = mock(TalkService::class.java)
    private val metricsClient = mock(MetricsClient::class.java)
    private val buzzClient = mock(BuzzClient::class.java)
    private val service = TalkEngagementService(talkService, metricsClient, buzzClient)

    @Test
    fun `should await engagement with coroutine style`() = runTest {
        given(talkService.getTalk(21L)).willReturn(aTalk(21L, "Kotlin Coroutines and Reactor"))
        given(metricsClient.getViews(21L)).willReturn(Mono.just(50L))
        given(buzzClient.getBuzzScore(21L)).willReturn(Mono.just(0.8))

        val engagement = service.getEngagement(21L).awaitSingle()

        engagement.talkId shouldBe 21L
        engagement.views shouldBe 50L
        engagement.buzzScore shouldBe 0.8
        engagement.engagementScore shouldBe 74.0
    }

    private fun aTalk(id: Long, title: String) = TalkDto(
        id,
        title,
        "Abstract",
        TalkLevel.INTERMEDIATE,
        45,
        null,
        emptyList(),
        emptyList(),
        emptyList(),
        null,
        null,
        null
    )
}
