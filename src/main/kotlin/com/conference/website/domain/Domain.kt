package com.conference.website.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant
import java.time.LocalDateTime

enum class TalkLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class EvaluationStatus {
    SUBMITTED,
    UNDER_REVIEW,
    ON_HOLD,
    ACCEPTED,
    REJECTED
}

@Entity
@Table(name = "speakers")
open class Speaker(

    @field:Column(nullable = false)
    open var name: String,

    @field:Column(nullable = false, unique = true)
    open var email: String,

    @field:Column(nullable = false)
    open var company: String,

    @field:Column(nullable = false, length = 2000)
    open var bio: String

) {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set
}

@Entity
@Table(name = "tags")
open class Tag(

    @field:Column(nullable = false, unique = true)
    open var name: String

) {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set
}

@Entity
@Table(name = "ratings")
open class Rating(

    @field:Column(nullable = false)
    open var reviewerName: String,

    @field:Column(nullable = false)
    open var score: Int,

    @field:Column(nullable = true, length = 2000)
    open var comment: String? = null

) {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @field:Column(nullable = false)
    open var createdAt: Instant? = null
        protected set

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "talk_id", nullable = false)
    open var talk: Talk? = null

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
    }
}

@Entity
@Table(name = "moderation_messages")
open class ModerationMessage(

    @field:Column(nullable = false)
    open var evaluatorName: String,

    @field:Column(nullable = false, length = 2000)
    open var message: String

) {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @field:Column(nullable = false)
    open var createdAt: Instant? = null
        protected set

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "talk_id", nullable = false)
    open var talk: Talk? = null

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
    }
}

@Entity
@Table(name = "schedule_slots")
open class ScheduleSlot(

    @field:Column(nullable = false)
    open var roomName: String,

    @field:Column(nullable = false)
    open var startTime: LocalDateTime,

    @field:Column(nullable = false)
    open var endTime: LocalDateTime

) {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set
}

@Entity
@Table(name = "talks")
open class   Talk(

    @field:Column(nullable = false)
    open var title: String,

    @field:Column(nullable = false, length = 4000)
    open var abstractText: String,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    open var level: TalkLevel,

    @field:Column(nullable = false)
    open var durationMinutes: Int,

    @field:ManyToOne(fetch = FetchType.LAZY, optional = false)
    @field:JoinColumn(name = "primary_speaker_id", nullable = false)
    open var primarySpeaker: Speaker,

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    open var evaluationStatus: EvaluationStatus = EvaluationStatus.SUBMITTED

) {

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @field:Column(nullable = false)
    open var createdAt: Instant? = null
        protected set

    @field:ManyToMany
    @field:JoinTable(
        name = "talk_co_speakers",
        joinColumns = [JoinColumn(name = "talk_id")],
        inverseJoinColumns = [JoinColumn(name = "speaker_id")]
    )
    open var coSpeakers: MutableSet<Speaker> = LinkedHashSet()

    @field:ManyToMany
    @field:JoinTable(
        name = "talk_tags",
        joinColumns = [JoinColumn(name = "talk_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    open var tags: MutableSet<Tag> = LinkedHashSet()

    @field:OneToMany(mappedBy = "talk", cascade = [CascadeType.ALL], orphanRemoval = true)
    open val ratings: MutableList<Rating> = ArrayList()

    @field:OneToMany(mappedBy = "talk", cascade = [CascadeType.ALL], orphanRemoval = true)
    open val moderationMessages: MutableList<ModerationMessage> = ArrayList()

    @field:OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @field:JoinColumn(name = "schedule_slot_id", unique = true)
    open var scheduleSlot: ScheduleSlot? = null

    @field:Version
    open var version: Long? = null
        protected set

    @PrePersist
    fun prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
    }

    fun addRating(rating: Rating) {
        rating.talk = this
        ratings.add(rating)
    }

    fun addModerationMessage(message: ModerationMessage) {
        message.talk = this
        moderationMessages.add(message)
    }
}
