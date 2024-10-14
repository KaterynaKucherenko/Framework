package com.mjc.school.service.implementation;


import com.mjc.school.repository.implementation.CommentRepository;

import com.mjc.school.repository.implementation.NewsRepository;
import com.mjc.school.repository.model.CommentModel;

import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.exceptions.ElementNotFoundException;
import com.mjc.school.service.exceptions.ValidatorException;
import com.mjc.school.service.interfaces.CommentServiceInterface;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.validation.CustomValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.exceptions.ErrorCodes.INVALID_VALUE_OF_SORTING;
import static com.mjc.school.service.exceptions.ErrorCodes.NO_COMMENTS_FOR_NEWS_ID;
import static com.mjc.school.service.exceptions.ErrorCodes.NO_COMMENT_WITH_PROVIDED_ID;

@Service("commentService")
@Transactional
public class CommentService implements CommentServiceInterface {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final NewsRepository newsRepository;
    private CustomValidator customValidator;

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, NewsRepository newsRepository, CustomValidator customValidator) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.newsRepository = newsRepository;
        this.customValidator = customValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDtoResponse> readAll(int page, int size, String sortBy) {
        try {
            return commentMapper.listModelToDtoList(commentRepository.readAll(page, size, sortBy));
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ValidatorException(String.format(INVALID_VALUE_OF_SORTING.getErrorMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDtoResponse readById(Long id) {
        Optional<CommentModel> opt = commentRepository.readById(id);
        return opt.map(commentMapper::ModelCommentToDto).orElseThrow(() -> new ElementNotFoundException(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), id)));

    }

    @Override
    @Transactional
    public CommentDtoResponse create(CommentDtoRequest createRequest) {
        customValidator.validateComment(createRequest);
        CommentModel commentModel = commentMapper.DtoCommentToModel(createRequest);
        NewsModel newsModel = newsRepository.readById(createRequest.newsId()).get();
        commentModel.setNewsModel(newsModel);
        CommentModel createCommentModel = commentRepository.create(commentModel);
        newsModel.addComment(createCommentModel);
        return commentMapper.ModelCommentToDto(createCommentModel);
    }

    @Override
    @Transactional
    public CommentDtoResponse update(Long id, CommentDtoRequest updateRequest) {
        if (commentRepository.existById(id)) {
            customValidator.validateComment(updateRequest);
            CommentModel commentModel = commentMapper.DtoCommentToModel(updateRequest);
            commentModel.setId(id);
            return commentMapper.ModelCommentToDto(commentRepository.update(commentModel));
        } else {
            throw new ElementNotFoundException(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (commentRepository.existById(id)) {
            return commentRepository.deleteById(id);
        } else {
            throw new ElementNotFoundException(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

    @Override
    public List<CommentDtoResponse> readListOfCommentsByNewsId(Long newsId) {
        if (newsId != null && newsId >= 0) {
            return commentMapper.listModelToDtoList(commentRepository.readListOfCommentsByNewsId(newsId));
        } else {
            throw new ElementNotFoundException(String.format(NO_COMMENTS_FOR_NEWS_ID.getErrorMessage(), newsId));


        }
    }
}

