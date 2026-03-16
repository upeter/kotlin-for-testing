package com.conference.website.repository;

import com.conference.website.domain.Speaker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    Optional<Speaker> findByEmailIgnoreCase(String email);

    @Query("select s from Speaker s where lower(s.email) in :emails")
    List<Speaker> findAllByEmailLowerIn(@Param("emails") Collection<String> emails);
}
