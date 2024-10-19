package com.mjc.school.repository.implementation;

import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.NewsPageModel;
import com.mjc.school.repository.model.TagModel;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("newsRepository")
public class NewsRepository extends AbstractDBRepository<NewsModel, Long> {
    public NewsPageModel readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content, int page, int pageSize) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NewsModel> query = criteriaBuilder.createQuery(NewsModel.class);
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<NewsModel> root = query.from(NewsModel.class);
        Root<NewsModel> countRoot = countQuery.from(NewsModel.class);

        List<Predicate> predicates = new ArrayList<>();

        if (tagName != null && !tagName.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tags");
            Join<NewsModel, TagModel> countJoinTags = countRoot.join("tags");
            predicates.add(criteriaBuilder.in(newsJoinTags.get("name")).value(tagName));
            predicates.add(criteriaBuilder.in(countJoinTags.get("name")).value(tagName));
        }
        if (tagId != null && !tagId.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tags");
            Join<NewsModel, TagModel> countJoinTags = countRoot.join("tags");
            predicates.add(criteriaBuilder.in(newsJoinTags.get("id")).value(tagId));
            predicates.add(criteriaBuilder.in(countJoinTags.get("id")).value(tagId));
        }
        if (authorName != null) {
            Join<NewsModel, AuthorModel> newsJoinAuthor = root.join("authorModel");
            Join<NewsModel, AuthorModel> countJoinAuthor = countRoot.join("authorModel");
            predicates.add(criteriaBuilder.equal(newsJoinAuthor.get("name"), authorName));
            predicates.add(criteriaBuilder.equal(countJoinAuthor.get("name"), authorName));
        }
        if (title != null || content != null) {
            Predicate titlePredicate = criteriaBuilder.like(root.get("title"), "%" + (title != null ? title : content) + "%");
            Predicate contentPredicate = criteriaBuilder.like(root.get("content"), "%" + (title != null ? title : content) + "%");
            predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate));
            Predicate countTitlePredicate = criteriaBuilder.like(countRoot.get("title"), "%" + (title != null ? title : content) + "%");
            Predicate countContentPredicate = criteriaBuilder.like(countRoot.get("content"), "%" + (title != null ? title : content) + "%");
            countQuery.where(criteriaBuilder.or(countTitlePredicate, countContentPredicate));
        }
        query.where(predicates.toArray(new Predicate[0]));
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        List<NewsModel> newsList = entityManager.createQuery(query)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        long totalNewsCount = entityManager.createQuery(countQuery).getSingleResult();
        return new NewsPageModel(newsList, totalNewsCount);
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
  
}
