package com.conference.website.dsl

import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag
import com.conference.website.domain.Talk
import com.conference.website.domain.TalkLevel
import java.util.LinkedHashSet

@DslMarker
annotation class TalkDslMarker

fun talk(block: TalkDsl.() -> Unit): Talk =
    TalkDsl().apply(block).build()

fun talks(block: TalksDsl.() -> Unit): List<Talk> =
    TalksDsl().apply(block).build()

fun speaker(block: SpeakerDsl.() -> Unit): Speaker =
    SpeakerDsl().apply(block).build()

@TalkDslMarker
class TalksDsl {
    private val talks = mutableListOf<Talk>()

    @Deprecated("Nested talks { } blocks are not supported", level = DeprecationLevel.ERROR)
    fun talks(block: TalksDsl.() -> Unit): Nothing =
        error("Nested talks { } blocks are not supported")

    internal fun build(): List<Talk> = talks.toList()
}

@TalkDslMarker
class TalkDsl {
    var title: String = "Kotlin for Java Developers"
    var abstractText: String = "Learn Kotlin in 20 minutes"
    var level: TalkLevel = TalkLevel.BEGINNER
    var durationMinutes: Int = 20

    private var primarySpeaker: Speaker? = null
    private val coSpeakers = mutableListOf<Speaker>()
    private val tags = mutableListOf<Tag>()

    fun primarySpeaker(block: SpeakerDsl.() -> Unit) {
        primarySpeaker = SpeakerDsl().apply(block).build()
    }

    fun primarySpeaker(speaker: Speaker) {
        primarySpeaker = speaker
    }

    fun coSpeaker(block: SpeakerDsl.() -> Unit) {
        coSpeakers += SpeakerDsl().apply(block).build()
    }

    fun coSpeakers(vararg speakers: Speaker) {
        coSpeakers += speakers
    }

    fun tag(block: TagDsl.() -> Unit) {
        tags += TagDsl().apply(block).build()
    }

    fun tag(tag: Tag) {
        tags += tag
    }

    fun tag(name: String) {
        tags += TagDsl().apply { this.name = name }.build()
    }

    fun tags(vararg names: String) {
        names.forEach(::tag)
    }

    @Deprecated("talk { } is only valid inside talks { }", level = DeprecationLevel.ERROR)
    fun talk(block: TalkDsl.() -> Unit): Nothing =
        error("talk { } is only valid inside talks { }")

    @Deprecated("talks { } cannot be nested inside talk { }", level = DeprecationLevel.ERROR)
    fun talks(block: TalksDsl.() -> Unit): Nothing =
        error("talks { } cannot be nested inside talk { }")


    internal fun build(): Talk {
        val requiredPrimarySpeaker = requireNotNull(primarySpeaker) {
            "primarySpeaker { ... } is required in talk { ... }"
        }

        return Talk(
            title,
            abstractText,
            level,
            durationMinutes,
            requiredPrimarySpeaker
        ).apply {
            setCoSpeakers(LinkedHashSet(this@TalkDsl.coSpeakers))
            setTags(LinkedHashSet(this@TalkDsl.tags))
        }
    }
}

@TalkDslMarker
class SpeakerDsl {
    var name: String = "Ada Lovelace"
    var email: String = "ada@example.com"
    var company: String = "Analytical Engines"
    var bio: String = "Pioneer in computing"

    internal fun build(): Speaker = Speaker(name, email, company, bio)
}

@TalkDslMarker
class TagDsl {
    var name: String = "java"

    internal fun build(): Tag = Tag(name)
}
