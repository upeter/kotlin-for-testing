package com.conference.website.service;

import com.conference.website.dto.CreateTagsRequest;
import com.conference.website.dto.TagDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class E10_TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    void shouldCreateTag() {
        //Arrange, Act
        List<TagDto> createdTags = tagService.createTags(
           new CreateTagsRequest(List.of("Java")));

        //Assert
        assertEquals(2, createdTags.size());
        var firstTag = createdTags.getFirst();
        assertEquals("java", firstTag.name());
        assertNotNull(firstTag.id());
    }



















    @Test
    void shouldRejectDuplicateTagNames() {
        List<TagDto> createdTags = tagService
           .createTags(new CreateTagsRequest(
              List.of("java", "kotlin")));

        assertThat(createdTags)
           .hasSize(3)
                .allSatisfy(tag ->
                   assertThat(tag.id()).isNotNull())
                .extracting(TagDto::name)
           .containsExactlyInAnyOrder("java", "kotlin") ;

        assertThatThrownBy(() -> tagService
           .createTags(new CreateTagsRequest(List.of("Java"))))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Tag already exists")
                .hasMessageContaining("java");
    }

}







