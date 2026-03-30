package com.conference.website.utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll as awaitAllDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import reactor.core.publisher.Mono


suspend fun <T : Any> List<Mono<T>>.awaitAll() = coroutineScope {
    map { mono -> async { mono.awaitSingle() } }.awaitAllDeferred()
}
