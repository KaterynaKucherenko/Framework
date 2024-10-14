package com.mjc.school.repository.implementation;

import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.TagModel;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository("newsRepository")
public class NewsRepository extends AbstractDBRepository<NewsModel, Long> {
    public List<NewsModel> readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NewsModel> query = criteriaBuilder.createQuery(NewsModel.class);
        Root<NewsModel> root = query.from(NewsModel.class);
        if (tagName != null && !tagName.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tags");
            Predicate tagN = criteriaBuilder.equal(newsJoinTags.get("name"), tagName);
            query.select(root).where(tagN);
        }
        if (tagId != null && !tagId.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tags");
            Predicate tagI = criteriaBuilder.in(newsJoinTags.get("id")).value(tagId);
            query.select(root).where(tagI);
        }
        if (authorName != null) {
            Join<NewsModel, AuthorModel> newsJoinAuthor = root.join("authorModel");
            Predicate authorN = criteriaBuilder.equal(newsJoinAuthor.get("name"),  authorName );
            query.select(root).where(authorN);
        }
        if (title != null) {
            Predicate titlePart = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            query.select(root).where(titlePart);
        }
        if (content != null) {
            Predicate contentPart = criteriaBuilder.like(root.get("content"), "%" + content + "%");
            query.select(root).where(contentPart);
        }
        TypedQuery<NewsModel> result = entityManager.createQuery(query);
        return result.getResultList();
    }

    @Override
    void update(NewsModel prevState, NewsModel nextState) {
        prevState.setTitle(nextState.getTitle());

        prevState.setContent(nextState.getContent());

        AuthorModel authorModel = nextState.getAuthorModel();
        prevState.setAuthorModel(nextState.getAuthorModel());

        List<TagModel> tagModels = nextState.getTags();
        prevState.setTags(nextState.getTags());

    }

    public Optional<NewsModel> readNewsByTitle(String title) {
        TypedQuery<NewsModel> typedQuery = entityManager.createQuery("SELECT a FROM NewsModel a WHERE a.title LIKE:title", NewsModel.class).setParameter("title", title);
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Long totalNewsCount() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<NewsModel> newsModelRootRoot = criteriaQuery.from(NewsModel.class);
        criteriaQuery.select(builder.count(newsModelRootRoot));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}
