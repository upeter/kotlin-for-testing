package com.conference.website.integration

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class FakeBuzzClient : BuzzClient {

    override fun getBuzzScore(talkId: Long): Mono<Double> {
        if (talkId % 13 == 0L) {
            return Mono.delay(LATENCY)
                .flatMap { Mono.error<Double>(IllegalStateException("Buzz stream unavailable")) }
        }

        val score = ((talkId % 10) + 1) / 10.0
        return Mono.just(score)
            .delayElement(LATENCY)
    }

    companion object {
        private val LATENCY: Duration = Duration.ofMillis(55)
    }
}
