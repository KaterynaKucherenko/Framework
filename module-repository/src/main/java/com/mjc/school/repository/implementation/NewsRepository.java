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
    public NewsPageModel readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content, int page, int pageSize, String sortBy) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NewsModel> query = criteriaBuilder.createQuery(NewsModel.class);
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<NewsModel> root = query.from(NewsModel.class);
        Root<NewsModel> countRoot = countQuery.from(NewsModel.class);

        Predicate predicates = criteriaBuilder.conjunction();

        if (tagName != null && !tagName.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tags");
            predicates = criteriaBuilder.and(predicates, newsJoinTags.get("name").in(tagName));
        }
        if (tagId != null && !tagId.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tags");
            predicates = criteriaBuilder.and(predicates, newsJoinTags.get("id").in(tagId));
        }
        if (authorName != null) {
            Join<NewsModel, AuthorModel> newsJoinAuthor = root.join("authorModel");
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(newsJoinAuthor.get("name"), authorName));
        }
        if ((title != null && !title.isEmpty()) || (content != null && !content.isEmpty())) {
            Predicate titlePredicate = null;
            Predicate contentPredicate = null;
            if (title != null && !title.isEmpty()) {
                titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title + "%");
            }
            if (content != null && !content.isEmpty()) {
                contentPredicate = criteriaBuilder.equal(root.get("content"), "%" + content + "%");
            }

            if (titlePredicate != null && contentPredicate != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.or(titlePredicate, contentPredicate));
            } else if (title != null) {
                predicates = criteriaBuilder.and(predicates, titlePredicate);
            } else if (content != null) {
                predicates = criteriaBuilder.and(predicates, contentPredicate);
            }
        }
        query.where(predicates);
        countQuery.select(criteriaBuilder.count(root)).where(predicates);

        if (sortBy != null && !sortBy.isEmpty()) {
            String[] sortByArray = sortBy.split(",");
            if (sortByArray.length == 2) {
                String sort = sortByArray[0];
                String typeSorting = sortByArray[1].equalsIgnoreCase("asc") ? "asc" : "desc";
                if (typeSorting.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(root.get(sort)));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(sort)));
                }
            }
        }
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

    public Long totalNewsCount() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<NewsModel> newsModelRootRoot = criteriaQuery.from(NewsModel.class);
        criteriaQuery.select(builder.count(newsModelRootRoot));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

}
