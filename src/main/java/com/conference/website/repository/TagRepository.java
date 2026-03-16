package com.conference.website.repository;

import com.conference.website.domain.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    @Query("select t from Tag t where lower(t.name) in :names")
    List<Tag> findAllByNameLowerIn(@Param("names") Collection<String> names);
}
