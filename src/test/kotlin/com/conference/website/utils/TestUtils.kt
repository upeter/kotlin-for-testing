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

inline fun <reified T> T.toJson(): String = objectMapper.writeValueAsString(this)

inline fun <reified T:Any> MvcResult.readBody(): T =
    objectMapper.readValue<T>(response.contentAsByteArray).shouldNotBeNull()

inline fun <reified T:Any> ResultActions.readBody(): T = andReturn().readBody()

inline fun <reified T> MockHttpServletRequestBuilder.jsonContent(obj: T): MockHttpServletRequestBuilder =
    this.contentType(MediaType.APPLICATION_JSON).content(obj.toJson())
