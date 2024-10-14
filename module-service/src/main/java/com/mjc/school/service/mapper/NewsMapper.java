package com.mjc.school.service.mapper;


import com.mjc.school.repository.implementation.AuthorRepository;
import com.mjc.school.repository.implementation.CommentRepository;
import com.mjc.school.repository.implementation.TagRepository;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TagMapper.class, CommentMapper.class, AuthorMapper.class})
public abstract class NewsMapper {
    @Autowired
    protected AuthorRepository authorRepository;
    @Autowired
    protected TagRepository tagRepository;
    @Autowired
    protected CommentRepository commentRepository;

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorModel", expression = "java(authorRepository.readAuthorByName(newsDtoRequest.authorName()).get())")
    @Mapping(target = "tags", expression = "java(newsDtoRequest.tagNames().stream().map(name -> tagRepository.readTagByName(name).get()).toList())")
    public abstract NewsModel DTONewsToModel(NewsDtoRequest newsDtoRequest);

    @Mapping(source = "tags", target = "tagList")
    @Mapping(source = "comments", target = "commentList")
    @Mapping(target = "authorDtoResponse", source = "authorModel")
    @Mapping(target = "createDate", expression = "java(newsModel.getCreateDate().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))")
    @Mapping(target = "lastUpdateDate", expression = "java(newsModel.getLastUpdateDate().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))")
    public abstract NewsDtoResponse ModelNewsToDTO(NewsModel newsModel);

    public abstract List<NewsDtoResponse> ModelListToDtoList(List<NewsModel> newsModelList);


}
