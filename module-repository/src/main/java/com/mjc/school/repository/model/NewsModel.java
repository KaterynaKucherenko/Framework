package com.mjc.school.repository.model;

import com.mjc.school.repository.interfaces.BaseEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "news")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NewsModel implements BaseEntity<Long>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false, name = "title", unique = true)
    private String title;
    @Column(nullable = false, name = "content")
    private String content;
    @CreatedDate
    @Column(name = "createDate")
    private LocalDateTime createDate;
    @LastModifiedDate
    @Column(name = "lastUpdateDate")
    private LocalDateTime lastUpdateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorModel authorModel;

    @OneToMany(mappedBy = "newsModel", cascade = CascadeType.REMOVE)
    private List<CommentModel> comments = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tags_of_news", joinColumns = @JoinColumn(name = "news_id"), inverseJoinColumns = @JoinColumn(name = "tags_id"))
    private List<TagModel> tags = new ArrayList<>();

    public NewsModel() {

    }
    public NewsModel(Long id, String title, String content, AuthorModel authorModel) {

    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }


    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }


    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }


    @Override
    public Long getId() {
        return id;
    }

    @Override

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        NewsModel newsModel = (NewsModel) obj;
        return id == newsModel.id &&
                (title == newsModel.title || (title != null && title.equals(newsModel.getTitle()))) &&
                (content == newsModel.content || (content != null && content.equals(newsModel.getContent()))) &&
                (createDate == newsModel.createDate || (createDate != null && createDate.equals(newsModel.getCreateDate()))) &&
                (lastUpdateDate == newsModel.lastUpdateDate || (lastUpdateDate != null && lastUpdateDate.equals(newsModel.getLastUpdateDate()))
                );
    }

    public int hashCode() {
        return Objects.hash(id, title, content, createDate, lastUpdateDate);
    }

    public String toString() {
        return "news ID: " + id + ", title: " + title + ", content: " + content + ", create date: " + createDate + ", last update date: " + lastUpdateDate;
    }


    public void setTags(List<TagModel> tags) {
        this.tags = tags;
    }

    public List<TagModel> getTags() {
        return tags;
    }

    public AuthorModel getAuthorModel() {
        return authorModel;
    }

    public void setAuthorModel(AuthorModel authorModel) {
        this.authorModel = authorModel;
    }

    public List<CommentModel> getComments() {
        return comments;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }

    public void addComment(CommentModel commentModel) {
        comments.add(commentModel);
    }
}