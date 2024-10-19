package com.mjc.school.repository.model;

import java.util.List;

public class NewsPageModel {
    private List<NewsModel> newsList;
    private long totalNewsCount;

    public NewsPageModel(List<NewsModel> newsList, long totalNewsCount) {
        this.newsList = newsList;
        this.totalNewsCount = totalNewsCount;
    }

    public List<NewsModel> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<NewsModel> newsList) {
        this.newsList = newsList;
    }

    public long getTotalNewsCount() {
        return totalNewsCount;
    }

    public void setTotalNewsCount(long totalNewsCount) {
        this.totalNewsCount = totalNewsCount;
    }
}
