package com.mjc.school.controller.implementation;


import com.mjc.school.controller.annotation.CommandParam;
import com.mjc.school.controller.hateoas.LinkHelper;
import com.mjc.school.controller.interfaces.NewsControllerInterface;
import com.mjc.school.service.dto.*;
import com.mjc.school.service.interfaces.AuthorServiceInterface;
import com.mjc.school.service.interfaces.CommentServiceInterface;
import com.mjc.school.service.interfaces.NewsServiceInterface;
import com.mjc.school.service.interfaces.TagServiceInterface;
import io.swagger.annotations.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/news", produces = "application/json")
@Api(value = "News", description = "Operations for creating, updating, retrieving and deleting news in the application")
public class NewsController implements NewsControllerInterface<NewsDtoRequest, NewsDtoResponse, Long> {

    private final NewsServiceInterface <NewsDtoRequest, NewsDtoResponse, Long> newsService;
    private final AuthorServiceInterface authorService;
    private final TagServiceInterface tagService;
    private final CommentServiceInterface commentService;

    public NewsController(NewsServiceInterface newsService, AuthorServiceInterface authorService, TagServiceInterface tagService, CommentServiceInterface commentService) {
        this.newsService = newsService;
        this.authorService = authorService;
        this.tagService = tagService;
        this.commentService = commentService;
    }

    @GetMapping
    @Override
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all news", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched all news"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<NewsPageDtoResponse> readAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createDate,dsc") String sortBy) {
        NewsPageDtoResponse response = newsService.readAll(page, size, sortBy);  // Запрос к сервису
        EntityModel<NewsPageDtoResponse> model = EntityModel.of(response);
        return model;
    }

    @Override
    @GetMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get news by ID", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched news by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<NewsDtoResponse> readById(@CommandParam("newsId") @PathVariable Long id) {
        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.readById(id));
        LinkHelper.addLinkToNews(model);
        return model;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a news", response = NewsDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a news"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<NewsDtoResponse> create(@RequestBody NewsDtoRequest createRequest) {
        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.create(createRequest));
        LinkHelper.addLinkToNews(model);
        return model;
    }

    @Override
    @PatchMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update a news", response = NewsDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a news"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<NewsDtoResponse> update(@PathVariable Long id, @RequestBody NewsDtoRequest updateRequest) {
        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.update(id, updateRequest));
        LinkHelper.addLinkToNews(model);
        return model;
    }

    @Override
    @DeleteMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete news by ID")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted news by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public void deleteById(@CommandParam("newsId") @PathVariable Long id) {
        newsService.deleteById(id);
    }


    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get news with provided parameters", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched news with provided parameters"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<NewsDtoResponse> readListOfNewsByParams(
            @RequestParam(name = "tag_name", required = false) List<String> tagName,
            @RequestParam(name = "tag_id", required = false)
            @ApiParam(type = "Long", format = "int64")
            List<Long> tagId,
            @RequestParam(name = "author_name", required = false) String authorName,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "content", required = false) String content) {
        return newsService.readListOfNewsByParams(tagName, tagId, authorName, title, content);
    }

    @GetMapping(value = "/{newsId:\\d+}/tag")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get tags of provided news", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched tags of provided news"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<EntityModel<TagDtoResponse>> readListOfTagsByNewsId(@PathVariable Long newsId) {
        List<EntityModel<TagDtoResponse>> tagModels = tagService.readListOfTagsByNewsId(newsId).stream().map(EntityModel::of).toList();
        tagModels.forEach(LinkHelper::addLinkToTags);
        return tagModels;

    }


    @GetMapping(value = "/{newsId:\\d+}/author")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get author of provided news", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched author of provided news"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<AuthorDtoResponse> readAuthorByNewsId(@PathVariable Long newsId) {
        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.readAuthorByNewsId(newsId));
        LinkHelper.addLinkToAuthors(model);
        return model;
    }

    @GetMapping(value = "/{newsId:\\d+}/comment")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get comments of provided news", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched comments of provided news"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<EntityModel<CommentDtoResponse>> readListOfCommentsByNewsId(@PathVariable Long newsId) {
        List<EntityModel<CommentDtoResponse>> commentModels = commentService.readListOfCommentsByNewsId(newsId).stream().map(EntityModel::of).toList();
        commentModels.forEach(LinkHelper::addLinkToComments);
        return commentModels;
    }

}
