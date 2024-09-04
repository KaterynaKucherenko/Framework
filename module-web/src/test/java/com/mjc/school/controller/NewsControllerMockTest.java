package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.implementation.CommentService;
import com.mjc.school.service.implementation.NewsService;
import org.hibernate.mapping.Map;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class NewsControllerMockTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    private ObjectMapper objectMapper;

    private final String NEWS_TITLE = "The Integrity";
    private final String NEWS_CONTENT = "The Populist Wave and Its Discontents";
    private final String AUTHOR_NAME = "Jacob Mc.Ir";
    private final AuthorDtoResponse AUTHOR_RESP = new AuthorDtoResponse(3L, AUTHOR_NAME, null, null);
    private final List<String> TAGS_NAMES = Arrays.asList("videoFiles", "audioFiles");
    private final  List<TagDtoResponse> LIST_OF_TAGS = Arrays.asList(new TagDtoResponse(1L, "videoFiles"), new TagDtoResponse(2L, "audioFiles"));
    private final List<CommentDtoResponse> LIST_OF_COMMENTS = Arrays.asList(new CommentDtoResponse(1L, "Not bad!", null, null, 1L), new CommentDtoResponse(2L, "Incredible!", null, null, 2L)
    );
    private NewsDtoRequest newsDtoRequest;
    private NewsDtoResponse newsDtoResponse;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        newsDtoRequest = new NewsDtoRequest(NEWS_TITLE, NEWS_CONTENT, AUTHOR_NAME, TAGS_NAMES);
        newsDtoResponse = new NewsDtoResponse(1L, NEWS_TITLE, NEWS_CONTENT, null, null, AUTHOR_RESP, LIST_OF_TAGS, LIST_OF_COMMENTS);
    }

    @Test
    public void readAllTest() throws Exception {
        List<NewsDtoResponse> news = Arrays.asList(
                new NewsDtoResponse(1L, NEWS_TITLE, NEWS_CONTENT, null, null, AUTHOR_RESP, LIST_OF_TAGS, LIST_OF_COMMENTS),
                new NewsDtoResponse(2L, "News_title_example", "News_content_example",null, null, new AuthorDtoResponse(6L, "Amicia", null, null), LIST_OF_TAGS, LIST_OF_COMMENTS)
        );

        when(newsService.readAll(anyInt(), anyInt(), anyString())).thenReturn(news);

        mockMvc.perform(get("/api/v1/news")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "createDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo(NEWS_TITLE)))
                .andExpect(jsonPath("$[0].content", equalTo(NEWS_CONTENT)));
//                .andExpect(jsonPath("$[0].name", equalTo("Author1")))
//                .andExpect(jsonPath("$[1].id", equalTo(2)))
//                .andExpect(jsonPath("$[1].name", equalTo("Author2")));
    }

//    @Test
//    public void readAuthorByIdTest() throws Exception {
//        when(authorService.readById(2L)).thenReturn(authorDtoResponse);
//        mockMvc.perform(get("/api/v1/author/{id}", 2L)).
//                andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", equalTo(2)))
//                .andExpect(jsonPath("$.name", equalTo(AUTHOR_NAME)));
//    }
//
//    @Test
//    @WithMockUser(authorities = "ROLE_USER")
//    public void createAuthorTest() throws Exception {
//        when(authorService.create(authorDtoRequest)).thenReturn(authorDtoResponse);
//        String authorJson = objectMapper.writeValueAsString(authorDtoRequest);
//        mockMvc.perform(post("/api/v1/author").contentType(MediaType.APPLICATION_JSON).content(authorJson))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id", equalTo(2)))
//                .andExpect(jsonPath("$.name", equalTo(AUTHOR_NAME)));
//    }
//
//    @Test
//    public void unauthorisedCreateAuthorTest() throws Exception {
//        when(authorService.create(authorDtoRequest)).thenReturn(authorDtoResponse);
//        String authorJson = objectMapper.writeValueAsString(authorDtoRequest);
//        mockMvc.perform(post("/api/v1/author").contentType(MediaType.APPLICATION_JSON).content(authorJson))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser (authorities = "ROLE_ADMIN")
//    public void deleteAuthorTest() throws Exception {
//        when(authorService.deleteById(authorDtoResponse.id())).thenReturn(true);
//        mockMvc.perform(delete("/api/v1/author/{id}", 2))
//                .andExpect(status().isNoContent());
//
//    }
//
//    @Test
//    public void unauthorisedDeleteAuthorTest() throws Exception {
//        when(authorService.deleteById(authorDtoResponse.id())).thenReturn(true);
//        mockMvc.perform(delete("/api/v1/author/{id}", 2))
//                .andExpect(status().isUnauthorized());
//    }
//    @Test
//    @WithMockUser (authorities = "ROLE_USER")
//    public void forbiddenDeleteAuthorTest() throws Exception {
//        when(authorService.deleteById(authorDtoResponse.id())).thenReturn(true);
//        mockMvc.perform(delete("/api/v1/author/{id}", 2))
//                .andExpect(status().isForbidden());
//    }
}
