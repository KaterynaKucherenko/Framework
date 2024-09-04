package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.implementation.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthorControllerMockTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    private ObjectMapper objectMapper;

    private final String AUTHOR_NAME = "Mickey";
    private AuthorDtoRequest authorDtoRequest;
    private AuthorDtoResponse authorDtoResponse;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        authorDtoRequest = new AuthorDtoRequest(AUTHOR_NAME);
        authorDtoResponse = new AuthorDtoResponse(2L, AUTHOR_NAME, null, null);
    }

    @Test
    public void readAllTest() throws Exception {
        List<AuthorDtoResponse> authors = Arrays.asList(
                new AuthorDtoResponse(1L, "Author1", null, null),
                new AuthorDtoResponse(2L, "Author2", null, null)
        );

        when(authorService.readAll(anyInt(), anyInt(), anyString())).thenReturn(authors);

        mockMvc.perform(get("/api/v1/author")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "name,dsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Author1")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].name", equalTo("Author2")));
    }

    @Test
    public void readAuthorByIdTest() throws Exception {
        when(authorService.readById(2L)).thenReturn(authorDtoResponse);
        mockMvc.perform(get("/api/v1/author/{id}", 2L)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(2)))
                .andExpect(jsonPath("$.name", equalTo(AUTHOR_NAME)));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createAuthorTest() throws Exception {
        when(authorService.create(authorDtoRequest)).thenReturn(authorDtoResponse);
        String authorJson = objectMapper.writeValueAsString(authorDtoRequest);
        mockMvc.perform(post("/api/v1/author").contentType(MediaType.APPLICATION_JSON).content(authorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(2)))
                .andExpect(jsonPath("$.name", equalTo(AUTHOR_NAME)));
    }

    @Test
    public void unauthorisedCreateAuthorTest() throws Exception {
        when(authorService.create(authorDtoRequest)).thenReturn(authorDtoResponse);
        String authorJson = objectMapper.writeValueAsString(authorDtoRequest);
        mockMvc.perform(post("/api/v1/author").contentType(MediaType.APPLICATION_JSON).content(authorJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser (authorities = "ROLE_ADMIN")
    public void deleteAuthorTest() throws Exception {
        when(authorService.deleteById(authorDtoResponse.id())).thenReturn(true);
mockMvc.perform(delete("/api/v1/author/{id}", 2))
        .andExpect(status().isNoContent());

    }

    @Test
    public void unauthorisedDeleteAuthorTest() throws Exception {
        when(authorService.deleteById(authorDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/author/{id}", 2))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser (authorities = "ROLE_USER")
    public void forbiddenDeleteAuthorTest() throws Exception {
        when(authorService.deleteById(authorDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/author/{id}", 2))
                .andExpect(status().isForbidden());
    }
}



