package com.conference.website.utils

import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient

inline fun <reified T : Any> RestTestClient.ResponseSpec.readBody(): T =
    returnResult(object:ParameterizedTypeReference<T>(){}).responseBody.shouldNotBeNull()

fun <S : RestTestClient.RequestHeadersSpec<S>> S.authorizationHeader(token: String = "default-token"): S =
    header("Authorization", "Bearer $token")

fun <S : RestTestClient.RequestHeadersSpec<S>> S.correlationIdHeader(correlationId: String = "default-correclationId"): S =
    header("X-Correlation-id", correlationId)

fun <S : RestTestClient.RequestHeadersSpec<S>> S.defaultHeaders(
    token: String = "default-token",
    correlationId: String = "default-correclationId",
): S = authorizationHeader(token).correlationIdHeader(correlationId)

fun <T : Any> RestTestClient.RequestBodySpec.jsonContent(obj: T): RestTestClient.RequestHeadersSpec<*> =
    contentType(MediaType.APPLICATION_JSON).body(obj)
