package com.conference.website.repository

import com.conference.website.domain.ModerationMessage
import com.conference.website.domain.Rating
import com.conference.website.domain.Speaker
import com.conference.website.domain.Tag
import com.conference.website.domain.Talk
import com.conference.website.domain.TalkLevel
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SpeakerRepository : JpaRepository<Speaker, Long> {

    fun findByEmailIgnoreCase(email: String): Speaker?

    @Query("select s from Speaker s where lower(s.email) in :emails")
    fun findAllByEmailLowerIn(@Param("emails") emails: Collection<String>): List<Speaker>
}

interface TagRepository : JpaRepository<Tag, Long> {

    fun findByNameIgnoreCase(name: String): Tag?

    @Query("select t from Tag t where lower(t.name) in :names")
    fun findAllByNameLowerIn(@Param("names") names: Collection<String>): List<Tag>
}

interface RatingRepository : JpaRepository<Rating, Long>

interface ModerationMessageRepository : JpaRepository<ModerationMessage, Long>

interface TalkRepository : JpaRepository<Talk, Long> {

    // moderationMessages is intentionally excluded here and left to lazy-load within the
    // transaction: Hibernate can't eager-fetch two List ("bag") collections in one query
    // (MultipleBagFetchException) and ratings already occupies that slot.
    @EntityGraph(attributePaths = ["primarySpeaker", "coSpeakers", "tags", "ratings", "scheduleSlot"])
    fun findDetailedById(id: Long): Talk?

    @EntityGraph(attributePaths = ["primarySpeaker", "tags", "scheduleSlot"])
    fun findByLevel(level: TalkLevel): List<Talk>

    @EntityGraph(attributePaths = ["primarySpeaker", "tags", "scheduleSlot"])
    fun findByTagsNameIgnoreCase(tagName: String): List<Talk>

    @EntityGraph(attributePaths = ["primarySpeaker", "tags", "scheduleSlot"])
    fun findAllByOrderByCreatedAtDesc(): List<Talk>
}
