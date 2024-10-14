package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerMockTest {
    String token = "mocked-jwt-token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private void performPostAndExpectToken(String url, Object request, String expectedToken) throws Exception {

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andReturn();
    }

    @Test
    void signUpShouldReturnJwtAuthenticationResponse() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest("testUsername", "testPassword123");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token);

        when(userService.signUp(signUpRequest)).thenReturn(response);

        performPostAndExpectToken("/sign-up", signUpRequest, token);
    }

    @Test
    void signIn_shouldReturnJwtAuthenticationResponse() throws Exception {
        SignInRequest signInRequest = new SignInRequest("testUsername", "testPassword123");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token);

        when(userService.signIn(signInRequest)).thenReturn(response);

        performPostAndExpectToken("/sign-in", signInRequest, token);
    }

}

