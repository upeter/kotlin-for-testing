package com.conference.website.integration

import reactor.core.publisher.Mono

interface BuzzClient {

    fun getBuzzScore(talkId: Long): Mono<Double>
}
