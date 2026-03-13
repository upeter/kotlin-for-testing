package com.conference.website.repository;

import com.conference.website.domain.Talk;
import com.conference.website.domain.TalkLevel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TalkRepository extends JpaRepository<Talk, Long> {

    @EntityGraph(attributePaths = {"primarySpeaker", "coSpeakers", "tags", "ratings", "scheduleSlot"})
    Optional<Talk> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"primarySpeaker", "tags", "scheduleSlot"})
    List<Talk> findByLevel(TalkLevel level);

    @EntityGraph(attributePaths = {"primarySpeaker", "tags", "scheduleSlot"})
    List<Talk> findByTagsNameIgnoreCase(String tagName);

    @EntityGraph(attributePaths = {"primarySpeaker", "tags", "scheduleSlot"})
    List<Talk> findAllByOrderByCreatedAtDesc();
}
