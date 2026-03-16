package com.conference.website.service;

import com.conference.website.api.dto.CreateTagsRequest;
import com.conference.website.domain.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TagServiceIT {

    @Autowired
    private TagService tagService;

    @Test
    void shouldRejectDuplicateTagNames() {
        List<Tag> createdTags = tagService.createTags(new CreateTagsRequest(List.of("java", "kotlin", "testing")));
        assertThat(createdTags)
                .hasSize(2)
                .allSatisfy(tag -> assertThat(tag.getId()).isNotNull())
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("java", "kotlin", "testing") ;

        assertThatThrownBy(() -> tagService.createTags(new CreateTagsRequest(List.of("java"))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Tag already exists")
                .hasMessageContaining("java");
    }

}
