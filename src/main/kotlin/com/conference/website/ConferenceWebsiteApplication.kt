package com.conference.website

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConferenceWebsiteApplication

fun main(args: Array<String>) {
    runApplication<ConferenceWebsiteApplication>(*args)
}
