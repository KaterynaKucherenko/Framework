package com.mjc.school.service.dto;

import java.util.List;

public class NewsPageDtoResponse {
    private List<NewsDtoResponse> newsList;
    private long totalNewsCount;

    public NewsPageDtoResponse(List<NewsDtoResponse> newsList, long totalNewsCount) {
        this.newsList = newsList;
        this.totalNewsCount = totalNewsCount;
    }

    public List<NewsDtoResponse> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<NewsDtoResponse> newsList) {
        this.newsList = newsList;
    }

    public long getTotalNewsCount() {
        return totalNewsCount;
    }

    public void setTotalNewsCount(long totalNewsCount) {
        this.totalNewsCount = totalNewsCount;
    }
}
