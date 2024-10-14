package com.mjc.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.implementation.AuthorService;
import com.mjc.school.service.implementation.CommentService;
import com.mjc.school.service.implementation.NewsService;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class NewsControllerMockTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private NewsService newsService;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private TagsService tagsService;
    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper;

    private final String NEWS_TITLE = "The Integrity";
    private final String NEWS_CONTENT = "The Populist Wave and Its Discontents";
    private final String AUTHOR_NAME = "Jacob Mc.Ir";
    private final AuthorDtoResponse AUTHOR_RESP = new AuthorDtoResponse(3L, AUTHOR_NAME, null, null);
    private final List<String> LIST_OF_TAGS_NAMES = Arrays.asList("videoFiles", "audioFiles");
    private final List<TagDtoResponse> LIST_OF_TAGS = Arrays.asList(new TagDtoResponse(1L, "videoFiles"), new TagDtoResponse(2L, "audioFiles"));
    private final List<CommentDtoResponse> LIST_OF_COMMENTS = Arrays.asList(new CommentDtoResponse(1L, "Not bad!", null, null, 1L), new CommentDtoResponse(2L, "Incredible!", null, null, 1L));
    private NewsDtoRequest newsDtoRequest;
    private NewsDtoResponse newsDtoResponse;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();

        newsDtoRequest = new NewsDtoRequest(NEWS_TITLE, NEWS_CONTENT, AUTHOR_NAME, LIST_OF_TAGS_NAMES);
        newsDtoResponse = new NewsDtoResponse(1L, NEWS_TITLE, NEWS_CONTENT, null, null, AUTHOR_RESP, LIST_OF_TAGS, LIST_OF_COMMENTS);
    }

    @Test
    public void readAllTest() throws Exception {
        List<NewsDtoResponse> news = Arrays.asList(
                new NewsDtoResponse(1L, NEWS_TITLE, NEWS_CONTENT, null, null, AUTHOR_RESP, LIST_OF_TAGS, LIST_OF_COMMENTS),
                new NewsDtoResponse(2L, "News_title_example", "News_content_example", null, null, new AuthorDtoResponse(6L, "Amicia", null, null), LIST_OF_TAGS, LIST_OF_COMMENTS)
        );
        NewsPageDtoResponse newsPageResponse = new NewsPageDtoResponse(news, 2L);
        when(newsService.readAll(anyInt(), anyInt(), anyString())).thenReturn(newsPageResponse);

        mockMvc.perform(get("/api/v1/news")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "createDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo(NEWS_TITLE)))
                .andExpect(jsonPath("$[0].content", equalTo(NEWS_CONTENT)))
                .andExpect(jsonPath("$[0].authorDtoResponse.name", equalTo(AUTHOR_NAME)))
                .andExpect(jsonPath("$[0].tagList[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].tagList[0].name", equalTo("videoFiles")))
                .andExpect(jsonPath("$[1].authorDtoResponse.name", equalTo("Amicia")))
                .andExpect(jsonPath("$[1].commentList[1].content", equalTo("Incredible!")));
    }

    @Test
    public void readANewsByIdTest() throws Exception {
        when(newsService.readById(1L)).thenReturn(newsDtoResponse);
        mockMvc.perform(get("/api/v1/news/{id}", 1L)).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.title", equalTo(NEWS_TITLE)))
                .andExpect(jsonPath("$.content", equalTo(NEWS_CONTENT)))
                .andExpect(jsonPath("$.authorDtoResponse.name", equalTo(AUTHOR_NAME)))
                .andExpect(jsonPath("$.tagList[1].id", equalTo(2)))
                .andExpect(jsonPath("$.tagList[1].name", equalTo("audioFiles")))
                .andExpect(jsonPath("$.commentList[0].content", equalTo("Not bad!")));
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void createNewsTest() throws Exception {
        when(newsService.create(newsDtoRequest)).thenReturn(newsDtoResponse);
        String newsJson = objectMapper.writeValueAsString(newsDtoRequest);
        mockMvc.perform(post("/api/v1/news").contentType(MediaType.APPLICATION_JSON).content(newsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.title", equalTo(NEWS_TITLE)))
                .andExpect(jsonPath("$.content", equalTo(NEWS_CONTENT)))
                .andExpect(jsonPath("$.authorDtoResponse.name", equalTo(AUTHOR_NAME)))
                .andExpect(jsonPath("$.tagList[1].id", equalTo(2)))
                .andExpect(jsonPath("$.tagList[1].name", equalTo("audioFiles")))
                .andExpect(jsonPath("$.commentList[0].content", equalTo("Not bad!")));
    }

    @Test
    public void unauthorisedCreateNewsTest() throws Exception {
        when(newsService.create(newsDtoRequest)).thenReturn(newsDtoResponse);
        String newsJson = objectMapper.writeValueAsString(newsDtoRequest);
        mockMvc.perform(post("/api/v1/news").contentType(MediaType.APPLICATION_JSON).content(newsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void unauthorisedUpdateNewsTest() throws Exception {
        when(newsService.update(1L, newsDtoRequest)).thenReturn(newsDtoResponse);
        String newsJson = objectMapper.writeValueAsString(newsDtoRequest);
        mockMvc.perform(patch("/api/v1/news/{id}", 1L).contentType(MediaType.APPLICATION_JSON).content(newsJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    public void deleteNewsTest() throws Exception {
        when(newsService.deleteById(newsDtoResponse.id())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/news/{id}", 1))
                .andExpect(status().isNoContent());

    }

    @Test
    public void getAuthorByNewsIdTest() throws Exception {
        when(authorService.readAuthorByNewsId(1L)).thenReturn(newsDtoResponse.authorDtoResponse());
        mockMvc.perform(get("/api/v1/news/{newsId}/author", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(3)))
                .andExpect(jsonPath("$.name", equalTo(AUTHOR_NAME)));
    }

    @Test
    public void getListOfTagsByNewsIdTest() throws Exception {
        when(tagsService.readListOfTagsByNewsId(1L)).thenReturn(newsDtoResponse.tagList());
        mockMvc.perform(get("/api/v1/news/{newsId}/tag", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].name", equalTo("videoFiles")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].name", equalTo("audioFiles")));
    }

    @Test
    public void readListOfCommentsByNewsIdTest() throws Exception {
        when(commentService.readListOfCommentsByNewsId(1L)).thenReturn(newsDtoResponse.commentList());
        mockMvc.perform(get("/api/v1/news/{newsId}/comment", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].content", equalTo("Not bad!")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].content", equalTo("Incredible!")));
    }

    @Test
    public void readListOfNewsByParams() throws Exception {
        when(newsService.readListOfNewsByParams(any(), any(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(newsDtoResponse));
        mockMvc.perform(get("/api/v1/news/search")
                        .param("tag_name", "videoFiles", "audioFiles")
                        .param("tag_id", String.valueOf(1L), String.valueOf(2L))
                        .param("author_name", "Jacob Mc.Ir")
                        .param("title", "The Integrity")
                        .param("content", "The Populist Wave and Its Discontents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo(NEWS_TITLE)))
                .andExpect(jsonPath("$[0].content", equalTo(NEWS_CONTENT)))
                .andExpect(jsonPath("$[0].authorDtoResponse.name", equalTo(AUTHOR_NAME)))
                .andExpect(jsonPath("$[0].tagList[1].id", equalTo(2)));
    }
}
