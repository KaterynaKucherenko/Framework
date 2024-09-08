package com.mjc.school.service.exceptions;

public enum ErrorCodes {
    NO_NEWS_WITH_PROVIDED_ID("40401", "News with this id: %d does not exist."),
    NO_AUTHOR_WITH_PROVIDED_ID("40402", "Author with this id: %d does not exist."),
    NO_TAG_WITH_PROVIDED_ID("40403", "Tag with this id: %d does not exist."),
    NO_COMMENT_WITH_PROVIDED_ID("40404", "Comment with this id: %d does not exist."),
    NO_AUTHOR_FOR_NEWS_ID("40405", "Author for news id: %d does not exist."),
    NO_TAGS_FOR_NEWS_ID("40406", "Tags for news id: %d do not exist."),
    NO_COMMENTS_FOR_NEWS_ID("40407", "Comments for news id: %d do not exist."),
    INVALID_VALUE_OF_SORTING("40408", "Value of the sortBy is wrong"),
    VALIDATION("40001", "Validation failed: %s."),
    UNEXPECTED_ERROR("00002", "Unexpected error happened on server"),
    NOT_UNIQUE_AUTHOR_NAME("00003", "Name of author does not unique."),
    NOT_UNIQUE_TAGS_NAME("00004", "Name of tag does not unique.");


    private final String errorMessage;

    ErrorCodes(String errorCode, String errMessage) {
        this.errorMessage = "errorMessage: " + errMessage + ",  errorCode: " + errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
