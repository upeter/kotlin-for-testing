package com.conference.website.repository;

import com.conference.website.domain.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    Optional<Speaker> findByEmailIgnoreCase(String email);
}
