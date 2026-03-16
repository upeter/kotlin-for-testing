package com.conference.website.api;

import com.conference.website.data.builders.CreateSpeakerRequestBuilder;
import com.conference.website.data.builders.SpeakerDtoBuilder;
import com.conference.website.dto.CreateSpeakerRequest;
import com.conference.website.dto.SpeakerDto;
import com.conference.website.service.BadRequestException;
import com.conference.website.service.SpeakerService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpeakerController.class)
@Import(ApiExceptionHandler.class)
class SpeakerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpeakerService speakerService;

    @Test
    void shouldCreateSpeaker() throws Exception {
        CreateSpeakerRequest request = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().build();
        SpeakerDto createdSpeaker = SpeakerDtoBuilder.aSpeakerDto().withId(42L).build();

        given(speakerService.createSpeaker(request)).willReturn(createdSpeaker);

        mockMvc.perform(post("/api/speakers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.email").value(request.email()))
                .andExpect(jsonPath("$.company").value(request.company()))
                .andExpect(jsonPath("$.bio").value(request.bio()));
    }

    @Test
    void shouldListSpeakers() throws Exception {
        List<SpeakerDto> speakers = List.of(
                SpeakerDtoBuilder.aSpeakerDto().withId(1L).withName("Ada Lovelace").build(),
                SpeakerDtoBuilder.aSpeakerDto().withId(2L).withName("Grace Hopper").withEmail("grace@example.com").build()
        );

        given(speakerService.getAllSpeakers()).willReturn(speakers);

        mockMvc.perform(get("/api/speakers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ada Lovelace"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Grace Hopper"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateSpeakerPayloadIsInvalid() throws Exception {
        String invalidRequestBody = """
                {
                  "name": "",
                  "email": "not-an-email",
                  "company": "",
                  "bio": ""
                }
                """;

        mockMvc.perform(post("/api/speakers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failure"))
                .andExpect(jsonPath("$.detail").value("Request validation failed"))
                .andExpect(jsonPath("$.violations").isArray());
    }

    @Test
    void shouldMapBadRequestExceptionFromService() throws Exception {
        CreateSpeakerRequest request = CreateSpeakerRequestBuilder.aCreateSpeakerRequest().build();
        given(speakerService.createSpeaker(request))
                .willThrow(new BadRequestException("Speaker email already exists: " + request.email()));

        mockMvc.perform(post("/api/speakers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.detail").value("Speaker email already exists: " + request.email()));
    }
}
