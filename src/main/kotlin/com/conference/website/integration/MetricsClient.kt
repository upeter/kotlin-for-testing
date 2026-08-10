package com.conference.website.integration

import reactor.core.publisher.Mono

interface MetricsClient {

    fun incrementViews(talkId: Long): Mono<Long>

    fun incrementLikes(talkId: Long): Mono<Long>

    fun incrementAttends(talkId: Long): Mono<Long>

    fun getViews(talkId: Long): Mono<Long>

    fun getLikes(talkId: Long): Mono<Long>

    fun getAttends(talkId: Long): Mono<Long>
}
