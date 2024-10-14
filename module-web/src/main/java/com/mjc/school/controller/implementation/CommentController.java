package com.mjc.school.controller.implementation;

import com.mjc.school.controller.interfaces.BaseController;
import com.mjc.school.controller.annotation.CommandParam;
import com.mjc.school.controller.hateoas.LinkHelper;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.interfaces.CommentServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/comment", produces = "application/json")
@Api(value = "Comments", description = "Operations for creating, updating, retrieving and deleting comment in the application")
public class CommentController implements BaseController<CommentDtoRequest, CommentDtoResponse, Long> {
    private final CommentServiceInterface commentService;

    @Autowired
    public CommentController(CommentServiceInterface commentService) {
        this.commentService = commentService;
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all comments", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched all comments"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<CommentDtoResponse> readAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "created,dsc") String sortBy) {
        return this.commentService.readAll(page, size, sortBy);

    }

    @Override
    @GetMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get comment by ID", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched comment by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<CommentDtoResponse> readById(@CommandParam("commentId") @PathVariable Long id) {
        EntityModel<CommentDtoResponse> model = EntityModel.of(commentService.readById(id));
        LinkHelper.addLinkToComments(model);
        return model;
    }


    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a comment", response = CommentDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a comment"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<CommentDtoResponse> create(@RequestBody CommentDtoRequest createRequest) {
        EntityModel<CommentDtoResponse> model = EntityModel.of(commentService.create(createRequest));
        LinkHelper.addLinkToComments(model);
        return model;
    }

    @Override
    @PatchMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update a comment", response = CommentDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a comment"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<CommentDtoResponse> update(@PathVariable Long id, @RequestBody CommentDtoRequest updateRequest) {
        EntityModel<CommentDtoResponse> model = EntityModel.of(commentService.update(id, updateRequest));
        LinkHelper.addLinkToComments(model);
        return model;
    }

    @Override
    @DeleteMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete comment by ID")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted comment by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public void deleteById(@CommandParam("commentId") @PathVariable Long id) {
        commentService.deleteById(id);
    }

}
