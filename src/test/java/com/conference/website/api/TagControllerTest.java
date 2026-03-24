package com.conference.website.api;

import com.conference.website.dto.CreateTagsRequest;
import com.conference.website.domain.Tag;
import com.conference.website.dto.TagDto;
import com.conference.website.service.BadRequestException;
import com.conference.website.service.TagService;
import com.jayway.jsonpath.TypeRef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@Import(ApiExceptionHandler.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TagService tagService;

    @Test
    void shouldCreateTags() throws Exception {
        CreateTagsRequest request = new CreateTagsRequest(List.of("java", "kotlin"));
        given(tagService.createTags(request)).willReturn(List.of(new Tag("java"), new Tag("kotlin")));

        var response = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var tags = objectMapper.readValue(response, new TypeReference<List<TagDto>>() {});
        assertThat(tags)
                .extracting(TagDto::name)
                .containsExactlyInAnyOrder("java", "kotlin");

    }

    @Test
    void shouldListTags() throws Exception {
        given(tagService.getAllTags()).willReturn(List.of(new Tag("java"), new Tag("testing")));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("java"))
                .andExpect(jsonPath("$[1].name").value("testing"));
    }

    @Test
    void shouldReturnBadRequestWhenNamesIsEmpty() throws Exception {
        CreateTagsRequest request = new CreateTagsRequest(List.of());

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failure"))
                .andExpect(jsonPath("$.detail").value("Request validation failed"));
    }

    @Test
    void shouldMapBadRequestExceptionFromService() throws Exception {
        CreateTagsRequest request = new CreateTagsRequest(List.of("java"));
        given(tagService.createTags(request)).willThrow(new BadRequestException("Tag already exists: [java]"));

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.detail").value("Tag already exists: [java]"));
    }
}
