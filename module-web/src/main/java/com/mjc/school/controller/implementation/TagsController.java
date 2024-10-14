package com.mjc.school.controller.implementation;

import com.mjc.school.controller.interfaces.BaseController;
import com.mjc.school.controller.annotation.CommandParam;
import com.mjc.school.controller.hateoas.LinkHelper;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.interfaces.TagServiceInterface;
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
@RequestMapping(value = "api/v1/tag", produces = "application/json")
@Api(value = "Tags", description = "Operations for creating, updating, retrieving and deleting tag in the application")
public class TagsController implements BaseController<TagDtoRequest, TagDtoResponse, Long> {
    private TagServiceInterface tagsService;

    @Autowired
    public TagsController(TagServiceInterface tagsService) {
        this.tagsService = tagsService;
    }

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all tags", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched all tags"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<TagDtoResponse> readAll(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                        @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                        @RequestParam(value = "sortBy", required = false, defaultValue = "name,asc") String sortBy) {
        return tagsService.readAll(page, size, sortBy);

    }

    @Override
    @GetMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get tag by ID", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched tag by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<TagDtoResponse> readById(@CommandParam("tagId") @PathVariable Long id) {
        EntityModel<TagDtoResponse> model = EntityModel.of(tagsService.readById(id));
        LinkHelper.addLinkToTags(model);
        return model;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a tag", response = TagDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created a tag"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<TagDtoResponse> create(@RequestBody TagDtoRequest createRequest) {
        EntityModel<TagDtoResponse> model = EntityModel.of(tagsService.create(createRequest));
        LinkHelper.addLinkToTags(model);
        return model;
    }

    @Override
    @PatchMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update a tag", response = TagDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated a tag"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<TagDtoResponse> update(@PathVariable Long id, @RequestBody TagDtoRequest updateRequest) {
        EntityModel<TagDtoResponse> model = EntityModel.of(tagsService.update(id, updateRequest));
        LinkHelper.addLinkToTags(model);
        return model;
    }

    @Override
    @DeleteMapping(value = "/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete tag by ID")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted tag by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public void deleteById(@CommandParam("tagId") @PathVariable Long id) {
        tagsService.deleteById(id);
    }

}
