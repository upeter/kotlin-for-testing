package com.conference.website.utils;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.ObjectMapper;

public class E06_MockMvcTestUtils {
    private E06_MockMvcTestUtils() {}

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static String performAndGetResponse(MockMvc mockMvc,
                                               MockHttpServletRequestBuilder requestBuilder,
                                               ResultMatcher resultMatcher) throws Exception {
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        resultMatcher.match(result);
        var response = result.getResponse();
        String body = response.getContentAsString();
        return body;
    }

    public static String performAndGetResponseWithHeaders(MockMvc mockMvc,
                                                          MockHttpServletRequestBuilder requestBuilder,
                                                          ResultMatcher resultMatcher) throws Exception {
        var witHeaders = requestBuilder.header("X-Correlation-Id", "1234567890").header("Authorization", "Bearer token");
        return performAndGetResponse(mockMvc, witHeaders, resultMatcher);
    }

    public static String performAndGetResponseWithHeaders(MockMvc mockMvc,
                                                          String correlationId,
                                                          String token,
                                                          MockHttpServletRequestBuilder requestBuilder, ResultMatcher resultMatcher) throws Exception {
        var witHeaders = requestBuilder.header("X-Correlation-Id", correlationId).header("Authorization", "Bearer "+ token);
        return performAndGetResponse(mockMvc, witHeaders, resultMatcher);
    }

        public static String performAndGetResponseWithHeaders(String correlationId,
                                                              MockMvc mockMvc,
                                                              MockHttpServletRequestBuilder requestBuilder,
                                                              ResultMatcher resultMatcher) throws Exception {
        return performAndGetResponseWithHeaders(mockMvc, correlationId, "token", requestBuilder, resultMatcher);
    }

    public static String performAndGetResponseWithHeaders(MockMvc mockMvc,
                                                          String token,
                                                          MockHttpServletRequestBuilder requestBuilder,
                                                          ResultMatcher resultMatcher) throws Exception {
        return performAndGetResponseWithHeaders(mockMvc, "1234567890", token, requestBuilder, resultMatcher);
    }


}

