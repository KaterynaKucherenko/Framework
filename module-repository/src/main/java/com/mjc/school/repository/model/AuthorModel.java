package com.mjc.school.repository.model;

import com.mjc.school.repository.interfaces.BaseEntity;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "author")
@EntityListeners(AuditingEntityListener.class)
public class AuthorModel implements BaseEntity<Long> {
    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @CreatedDate
    @Column(name = "createDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "lastUpdateDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdateDate;

    @OneToMany(mappedBy = "authorModel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<NewsModel> newsModelListWithId = new ArrayList<>();


    public AuthorModel() {
    }

    public AuthorModel(Long id, String name, LocalDateTime createDate, LocalDateTime lastUpdateDate) {
        this.id = id;
        this.name = name;
        this.createDate = createDate;
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

    public void setName(String name) {
        this.name = name;
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

    public List<NewsModel> getNewsModelListWithId() {
        return newsModelListWithId;
    }

    public void setNewsModelListWithId(List<NewsModel> newsModelListWithId) {
        this.newsModelListWithId = newsModelListWithId;


    }


    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        AuthorModel authorModel = (AuthorModel) obj;
        return id == authorModel.id &&
                (name == authorModel.name || (name != null && name.equals(authorModel.getName()))) &&
                (createDate == authorModel.createDate || (createDate != null && createDate.equals(authorModel.getCreateDate()))) &&
                (lastUpdateDate == authorModel.lastUpdateDate || (lastUpdateDate != null && lastUpdateDate.equals(authorModel.getLastUpdateDate())));
    }

    public int hashCode() {
        return Objects.hash(id, name, createDate, lastUpdateDate);
    }

    public String toString() {
        return "Author's ID: " + id + ", author's name: " + name + ", create date: " + createDate + ", last update date: " + lastUpdateDate;
    }


}