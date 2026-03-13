package com.conference.website.service;

import com.conference.website.api.dto.CreateTagRequest;
import com.conference.website.domain.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TagServiceIT {

    @Autowired
    private TagService tagService;

    @Test
    void shouldRejectDuplicateTagNames() {
        Tag createdTag = tagService.createTag(new CreateTagRequest("java"));

        assertThat(createdTag.getId()).isNotNull();
        assertThat(createdTag.getName()).isEqualTo("java");

        assertThatThrownBy(() -> tagService.createTag(new CreateTagRequest("java")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Tag already exists")
                .hasMessageContaining("java");
    }
}
