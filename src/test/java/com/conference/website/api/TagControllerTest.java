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
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.IntStream;

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
        //Arrange
        CreateTagsRequest request = new CreateTagsRequest(List.of("java", "kotlin"));
        List<TagDto> tagDtos = IntStream.range(0, request.names().size())
                .mapToObj(i -> new TagDto((long) i, request.names().get(i)))
                .toList();
        given(tagService.createTags(request)).willReturn(tagDtos);

        //Act
        var response = mockMvc.perform(post("/api/tags")
                        .header("X-Correlation-Id", "1234567890")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var tags = objectMapper.readValue(response, new TypeReference<List<TagDto>>() {});

        //Assert
        assertThat(tags)
                .extracting(TagDto::name)
                .containsExactlyInAnyOrder(request.names().toArray(new String[0]));

    }

    @Test
    void shouldListTags() throws Exception {
        given(tagService.getAllTags()).willReturn(List.of(new  TagDto(1L, "java"), new  TagDto(2L, "testing")));

        var response = mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var tags = objectMapper.readValue(response, new TypeReference<List<TagDto>>() {});
        assertThat(tags)
                .extracting(TagDto::name)
                .containsExactlyInAnyOrder("java", "testing");
    }

    @Test
    void shouldReturnBadRequestWhenNamesIsEmpty() throws Exception {
        CreateTagsRequest request = new CreateTagsRequest(List.of());

        var response = mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var errorResponse = objectMapper.readValue(response, ProblemDetail.class);
        assertEquals("Validation failure", errorResponse.getTitle());
        assertEquals("Request validation failed", errorResponse.getDetail());
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
