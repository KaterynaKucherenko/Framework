package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.implementation.CommentService;
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
public class CommentControllerMockTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;

    private final String COMMENT_CONTENT = "Awesome!";
    private CommentDtoRequest commentDtoRequest;
    private CommentDtoResponse commentDtoResponse;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        commentDtoRequest = new CommentDtoRequest(COMMENT_CONTENT, 1L);
        commentDtoResponse = new CommentDtoResponse(2L, COMMENT_CONTENT, null, null, 1L);
    }

    @Test
    public void readAllTest() throws Exception {
        List<CommentDtoResponse> comments = Arrays.asList(
                new CommentDtoResponse(1L, "Not bad!", null, null, 3L),
                new CommentDtoResponse(2L, "Incredible!", null, null, 4L)
        );

        when(commentService.readAll(anyInt(), anyInt(), anyString())).thenReturn(comments);

        mockMvc.perform(get("/api/v1/comment")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "created,dsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].content", equalTo("Not bad!")))
                .andExpect(jsonPath("$[0].newsId", equalTo(3)))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].content", equalTo("Incredible!")))
                .andExpect(jsonPath("$[1].newsId", equalTo(4)));
    }

    @Test
    public void readCommentByIdTest() throws Exception {
        when(commentService.readById(2L)).thenReturn(commentDtoResponse);
        mockMvc.perform(get("/api/v1/comment/{id}", 2L)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(2)))
                .andExpect(jsonPath("$.content", equalTo(COMMENT_CONTENT)))
                .andExpect(jsonPath("$.newsId", equalTo(1)));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createCommentTest() throws Exception {
        when(commentService.create(commentDtoRequest)).thenReturn(commentDtoResponse);
        String authorJson = objectMapper.writeValueAsString(commentDtoRequest);
        mockMvc.perform(post("/api/v1/comment").contentType(MediaType.APPLICATION_JSON).content(authorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(2)))
                .andExpect(jsonPath("$.content", equalTo(COMMENT_CONTENT)))
                .andExpect(jsonPath("$.newsId", equalTo(1)));
    }

    @Test
    public void unauthorisedCreateCommentTest() throws Exception {
        when(commentService.create(commentDtoRequest)).thenReturn(commentDtoResponse);
        String authorJson = objectMapper.writeValueAsString(commentDtoRequest);
        mockMvc.perform(post("/api/v1/comment").contentType(MediaType.APPLICATION_JSON).content(authorJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser (authorities = "ROLE_ADMIN")
    public void deleteCommentTest() throws Exception {
        when(commentService.deleteById(commentDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/comment/{id}", 2))
                .andExpect(status().isNoContent());

    }

}
