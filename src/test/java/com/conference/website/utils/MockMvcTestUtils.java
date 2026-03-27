package com.conference.website.utils;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

public class MockMvcTestUtils {
    private MockMvcTestUtils() {}

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String performAndGetResponse(MockMvc mockMvc, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        var response = result.getResponse();
        String body = response.getContentAsString();
        return body;
    }

    public static String performAndGetResponseWithHeaders(MockMvc mockMvc, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        var witHeaders = requestBuilder.header("X-Correlation-Id", "1234567890").header("Authorization", "Bearer token");
        return performAndGetResponse(mockMvc, witHeaders);
    }

    public static String performAndGetResponseWithHeaders(String correlationId, MockMvc mockMvc, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return performAndGetResponseWithHeaders(mockMvc, correlationId, "token", requestBuilder);

    }

    public static String performAndGetResponseWithHeaders(MockMvc mockMvc, String token, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return performAndGetResponseWithHeaders(mockMvc, "1234567890", token, requestBuilder);
    }

    public static String performAndGetResponseWithHeaders(MockMvc mockMvc, String correlationId, String token, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        var witHeaders = requestBuilder.header("X-Correlation-Id", correlationId).header("Authorization", "Bearer "+ token);
        return performAndGetResponse(mockMvc, witHeaders);
    }

}

