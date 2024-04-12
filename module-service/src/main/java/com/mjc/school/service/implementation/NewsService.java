package com.mjc.school.service.implementation;

import com.mjc.school.repository.implementation.AuthorRepository;
import com.mjc.school.repository.implementation.NewsRepository;
import com.mjc.school.repository.implementation.TagRepository;
import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.exceptions.ElementNotFoundException;
import com.mjc.school.service.exceptions.ErrorCodes;
import com.mjc.school.service.exceptions.ValidatorException;
import com.mjc.school.service.interfaces.NewsServiceInterface;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.validation.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ErrorCodes.NO_NEWS_WITH_PROVIDED_ID;
import static com.mjc.school.service.exceptions.ErrorCodes.VALIDATION;
import static com.mjc.school.service.validation.CustomValidator.*;

@Service("newsService")
@Transactional
public class NewsService implements NewsServiceInterface {
    private final NewsRepository newsRepository;
    private NewsMapper newsMapper;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private CustomValidator customValidator;

    @Autowired
    public NewsService(NewsRepository newsRepository, NewsMapper newsMapper, AuthorRepository authorRepository, TagRepository tagRepository, CustomValidator customValidator) {
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.customValidator = customValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsDtoResponse> readAll(int page, int size, String sortBy) {
        return newsMapper.ModelListToDtoList((newsRepository.readAll(page, size, sortBy)));
    }


    @Override
    @Transactional(readOnly = true)
    public NewsDtoResponse readById(Long id) {
        Optional<NewsModel> opt = newsRepository.readById(id);
        return opt.map(newsMapper::ModelNewsToDTO).orElseThrow(()-> new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), id)));

    }


    @Override
    @Transactional
    public NewsDtoResponse create(NewsDtoRequest createRequest) {
        customValidator.validateNews(createRequest);
        createNotExistAuthor(createRequest.authorName());
        createNotExistTags(createRequest.tagNames());
        NewsModel newsModel = newsMapper.DTONewsToModel(createRequest);
        return newsMapper.ModelNewsToDTO(newsRepository.create(newsModel));
    }


    @Override
    @Transactional
    public NewsDtoResponse update(Long id, NewsDtoRequest updateRequest) {
        if (newsRepository.existById(id)) {
            customValidator.validateNews(updateRequest);
            createNotExistAuthor(updateRequest.authorName());
            createNotExistTags(updateRequest.tagNames());
            NewsModel newsModel = newsMapper.DTONewsToModel(updateRequest);
            newsModel.setId(id);
            return newsMapper.ModelNewsToDTO(newsRepository.update(newsModel));
        } else {
            throw new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), id));
        }

    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (newsRepository.existById(id)) {
            return newsRepository.deleteById(id);
        } else {
            throw new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

@Override
    public List<NewsDtoResponse> readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content) {

        return newsMapper.ModelListToDtoList(newsRepository.readListOfNewsByParams(tagName, tagId, authorName, title, content));
    }

    public void createNotExistAuthor(String authorName){
        if(authorName!=null && !authorName.equals("")){
            if(authorName.length()<AUTHOR_NAME_MAX_LENGTH && authorName.length()>AUTHOR_NAME_MIN_LENGTH){
            if(authorRepository.readAuthorByName(authorName).isEmpty()){
                AuthorModel authorModel = new AuthorModel();
                authorModel.setName(authorName);
                authorRepository.create(authorModel);
            }}
            else {throw new ValidatorException(String.format(ErrorCodes.VALIDATION.getErrorMessage(), "Length of author`s name must be between 15 and 3"));
        }

    }}
    public void createNotExistTags(List<String> tagNames){
        tagNames.stream().filter(name -> tagRepository.readTagByName(name).isEmpty()).map(name -> {
            if (name.length()> TAG_NAME_MIN_LENGTH && name.length()<TAG_NAME_MAX_LENGTH){
            TagModel tagModel = new TagModel();
            tagModel.setName(name);
            return tagModel;}
            else {throw new ValidatorException(String.format(ErrorCodes.VALIDATION.getErrorMessage(), "Length of tag`s name must be between 15 and 3"));}
        }).forEach(tagRepository::create);
    }
}



