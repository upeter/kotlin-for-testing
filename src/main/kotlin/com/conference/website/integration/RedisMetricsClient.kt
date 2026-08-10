package com.conference.website.integration

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class RedisMetricsClient(
    private val metricsDatabase: RedisMetricsDatabase
) : MetricsClient {

    override fun incrementViews(talkId: Long): Mono<Long> =
        Mono.fromCallable { metricsDatabase.incrementAndGetViews(talkId) }
            .delayElement(WRITE_LATENCY)

    override fun incrementLikes(talkId: Long): Mono<Long> =
        Mono.fromCallable { metricsDatabase.incrementAndGetLikes(talkId) }
            .delayElement(WRITE_LATENCY)

    override fun incrementAttends(talkId: Long): Mono<Long> =
        Mono.fromCallable { metricsDatabase.incrementAndGetAttends(talkId) }
            .delayElement(WRITE_LATENCY)

    override fun getViews(talkId: Long): Mono<Long> =
        Mono.fromCallable { metricsDatabase.getViews(talkId) }
            .delayElement(READ_LATENCY)

    override fun getLikes(talkId: Long): Mono<Long> =
        Mono.fromCallable { metricsDatabase.getLikes(talkId) }
            .delayElement(READ_LATENCY)

    override fun getAttends(talkId: Long): Mono<Long> =
        Mono.fromCallable { metricsDatabase.getAttends(talkId) }
            .delayElement(READ_LATENCY)

    companion object {
        private val WRITE_LATENCY: Duration = Duration.ofMillis(40)
        private val READ_LATENCY: Duration = Duration.ofMillis(30)
    }
}
