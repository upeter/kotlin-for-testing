package com.conference.website.api

import com.conference.website.service.BadRequestException
import com.conference.website.service.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(exception: NotFoundException): ProblemDetail {
        val detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.message)
        detail.type = URI.create("https://conference.local/errors/not-found")
        detail.title = "Resource not found"
        return detail
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException): ProblemDetail {
        val detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message)
        detail.type = URI.create("https://conference.local/errors/bad-request")
        detail.title = "Invalid request"
        return detail
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ProblemDetail {
        val detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed")
        detail.type = URI.create("https://conference.local/errors/validation")
        detail.title = "Validation failure"
        detail.setProperty("violations", exception.fieldErrors
            .map { "${it.field}: ${it.defaultMessage}" })
        return detail
    }
}
