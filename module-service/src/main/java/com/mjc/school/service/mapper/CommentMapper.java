package com.mjc.school.service.mapper;


import com.mjc.school.repository.implementation.NewsRepository;
import com.mjc.school.repository.model.CommentModel;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {NewsMapper.class})
public abstract class CommentMapper {
    @Autowired
    protected NewsRepository newsRepository;

    @Mappings(value = {@Mapping(target = "created", ignore = true),
            @Mapping(target = "modified", ignore = true),
            @Mapping(target = "newsModel", ignore = true),
            @Mapping(target = "id", ignore = true)})
    public abstract CommentModel DtoCommentToModel(CommentDtoRequest commentDtoRequest);

    @Mapping(target = "newsId", expression = "java(commentModel.getNewsModel().getId())")
    @Mapping(target = "created", expression = "java(commentModel.getCreated().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))")
    @Mapping(target = "modified", expression = "java(commentModel.getModified().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))")
    public abstract CommentDtoResponse ModelCommentToDto(CommentModel commentModel);

    public abstract List<CommentDtoResponse> listModelToDtoList(List<CommentModel> command);

}
