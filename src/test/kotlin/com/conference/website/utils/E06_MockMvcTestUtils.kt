package com.conference.website.utils

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import tools.jackson.databind.ObjectMapper

/**
 * The inferior MockMvc helper: instead of one function with defaults, five
 * hand-written overloads. Two of them differ only in the *order* of their
 * parameters — `(mockMvc, token, …)` versus `(correlationId, mockMvc, …)` — so
 * picking the wrong one compiles silently and fails at runtime.
 *
 * Compare with `E06_MockMvcSuperchargedTestUtils.kt`, where `defaultHeaders(token =
 * "…", correlationId = "…")` covers every case with named and default arguments.
 */
object E06_MockMvcTestUtils {

    var objectMapper: ObjectMapper = ObjectMapper()

    fun performAndGetResponse(
        mockMvc: MockMvc,
        requestBuilder: MockHttpServletRequestBuilder,
        resultMatcher: ResultMatcher
    ): String {
        val result = mockMvc.perform(requestBuilder).andReturn()
        resultMatcher.match(result)
        val response = result.response
        return response.contentAsString
    }

    fun performAndGetResponseWithHeaders(
        mockMvc: MockMvc,
        requestBuilder: MockHttpServletRequestBuilder,
        resultMatcher: ResultMatcher
    ): String {
        val withHeaders = requestBuilder
            .header("X-Correlation-Id", "1234567890")
            .header("Authorization", "Bearer token")
        return performAndGetResponse(mockMvc, withHeaders, resultMatcher)
    }

    fun performAndGetResponseWithHeaders(
        mockMvc: MockMvc,
        correlationId: String,
        token: String,
        requestBuilder: MockHttpServletRequestBuilder,
        resultMatcher: ResultMatcher
    ): String {
        val withHeaders = requestBuilder
            .header("X-Correlation-Id", correlationId)
            .header("Authorization", "Bearer $token")
        return performAndGetResponse(mockMvc, withHeaders, resultMatcher)
    }

    fun performAndGetResponseWithHeaders(
        correlationId: String,
        mockMvc: MockMvc,
        requestBuilder: MockHttpServletRequestBuilder,
        resultMatcher: ResultMatcher
    ): String = performAndGetResponseWithHeaders(mockMvc, correlationId, "token", requestBuilder, resultMatcher)

    fun performAndGetResponseWithHeaders(
        mockMvc: MockMvc,
        token: String,
        requestBuilder: MockHttpServletRequestBuilder,
        resultMatcher: ResultMatcher
    ): String = performAndGetResponseWithHeaders(mockMvc, "1234567890", token, requestBuilder, resultMatcher)
}
