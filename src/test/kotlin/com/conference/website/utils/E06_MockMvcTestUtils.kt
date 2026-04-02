package com.conference.website.utils

import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

val objectMapper: ObjectMapper = jacksonObjectMapper()

fun MockHttpServletRequestBuilder.authorizationHeader(
        token: String = "default-token"): MockHttpServletRequestBuilder =
    this.header("Authorization", "Bearer $token")

fun MockHttpServletRequestBuilder.correlationIdHeader(
        correlationId: String = "default-correlationId"): MockHttpServletRequestBuilder =
    this.header("X-Correlation-id", correlationId)

fun MockHttpServletRequestBuilder.defaultHeaders(
        token: String = "default-token",
        correlationId: String = "default-correlationId "): MockHttpServletRequestBuilder =
    this.authorizationHeader(token).correlationIdHeader(correlationId)



fun <T> MockHttpServletRequestBuilder.jsonContent(obj: T): MockHttpServletRequestBuilder =
    this.contentType(MediaType.APPLICATION_JSON).content(obj.toJson())

fun <T> T.toJson(): String = objectMapper.writeValueAsString(this)






















inline fun <reified T:Any> MvcResult.readBody(): T =
    objectMapper.readValue<T>(response.contentAsByteArray)
        .shouldNotBeNull()

inline fun <reified T:Any> ResultActions.readBody(): T =
    andReturn().readBody()



























