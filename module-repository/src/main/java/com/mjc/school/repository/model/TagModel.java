package com.mjc.school.repository.model;

import com.mjc.school.repository.interfaces.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "tags")
public class TagModel implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<NewsModel> news = new ArrayList<>();

    public TagModel() {
    }
    public TagModel(String name) {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<NewsModel> getNews() {
        return news;
    }

    public void setNews(List<NewsModel> news) {
        this.news = news;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        TagModel tagModel = (TagModel) obj;
        return id == tagModel.id &&
                (name == tagModel.name || (name != null && name.equals(tagModel.getName())));
    }

    public int hashCode() {
        return Objects.hash(id, name);
    }

    public String toString() {
        return "Tag's ID: " + id + ", tag's name: " + name;
    }
}
