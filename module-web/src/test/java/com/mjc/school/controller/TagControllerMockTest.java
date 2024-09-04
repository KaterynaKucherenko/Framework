package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.implementation.TagsService;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class TagControllerMockTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private TagsService tagsService;

    private ObjectMapper objectMapper;

    private final String TAG_NAME = "audioFiles";
    private TagDtoRequest tagDtoRequest;
    private TagDtoResponse tagDtoResponse;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        tagDtoRequest = new TagDtoRequest(TAG_NAME);
        tagDtoResponse = new TagDtoResponse(1L, TAG_NAME);
    }
    @Test
    public void readAllTest() throws Exception {
        List<TagDtoResponse> tags = Arrays.asList(
                new TagDtoResponse(1L, "videoFiles"),
                new TagDtoResponse(2L, "audioFiles")
        );

        when(tagsService.readAll(anyInt(), anyInt(), anyString())).thenReturn(tags);

        mockMvc.perform(get("/api/v1/tag")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "name,dsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("videoFiles")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].name", equalTo("audioFiles")));
    }

    @Test
    public void readTagByIdTest() throws Exception {
        when(tagsService.readById(1L)).thenReturn(tagDtoResponse);
        mockMvc.perform(get("/api/v1/tag/{id}", 1L)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo(TAG_NAME)));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createTagTest() throws Exception {
        when(tagsService.create(tagDtoRequest)).thenReturn(tagDtoResponse);
        String tagJson = objectMapper.writeValueAsString(tagDtoRequest);
        mockMvc.perform(post("/api/v1/tag").contentType(MediaType.APPLICATION_JSON).content(tagJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo(TAG_NAME)));
    }

    @Test
    public void unauthorisedCreateTagTest() throws Exception {
        when(tagsService.create(tagDtoRequest)).thenReturn(tagDtoResponse);
        String authorJson = objectMapper.writeValueAsString(tagDtoRequest);
        mockMvc.perform(post("/api/v1/tag").contentType(MediaType.APPLICATION_JSON).content(authorJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser (authorities = "ROLE_ADMIN")
    public void deleteTagTest() throws Exception {
        when(tagsService.deleteById(tagDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/tag/{id}", 1))
                .andExpect(status().isNoContent());

    }

    @Test
    public void unauthorisedDeleteTagTest() throws Exception {
        when(tagsService.deleteById(tagDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/tag/{id}", 1))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser (authorities = "ROLE_USER")
    public void forbiddenDeleteTagTest() throws Exception {
        when(tagsService.deleteById(tagDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/tag/{id}", 1))
                .andExpect(status().isForbidden());
    }
}

