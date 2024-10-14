package com.mjc.school.service.interfaces;

import com.mjc.school.service.dto.NewsDtoRequest;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.NewsPageDtoResponse;

import java.util.List;

public interface NewsServiceInterface <NewsDtoRequest, NewsDtoResponse, Long>{
    List<NewsDtoResponse> readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content);

    NewsDtoResponse readById(Long id);

    NewsDtoResponse create(NewsDtoRequest createRequest);

    NewsDtoResponse update(Long id, NewsDtoRequest updateRequest);

    boolean deleteById(Long id);
    NewsPageDtoResponse readAll(int page, int size, String sortBy);
}
