package com.conference.website.utils

import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll as awaitAllDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import reactor.core.publisher.Mono
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue


suspend fun <T : Any> List<Mono<T>>.awaitAll() = coroutineScope {
    map { mono -> async { mono.awaitSingle() } }.awaitAllDeferred()
}
