package com.mjc.school.repository.implementation;

import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.repository.model.NewsModel;
import com.mjc.school.repository.model.NewsPageModel;
import com.mjc.school.repository.model.TagModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.List;
import java.util.Optional;

@Repository("newsRepository")
public class NewsRepository extends AbstractDBRepository<NewsModel, Long> {

    @Query("SELECT n FROM NewsModel n JOIN n.authorModel a WHERE " +  "(:authorName IS NULL OR a.name LIKE %:authorName%)")
    public NewsPageModel readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content, int page, int pageSize, String sortBy) {

        System.out.println(tagName);
        System.out.println(tagId);
        System.out.println(authorName);
        System.out.println(title);
        System.out.println(content);
        System.out.println(page);
        System.out.println(pageSize);
        System.out.println(sortBy);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NewsModel> query = criteriaBuilder.createQuery(NewsModel.class);

        Root<NewsModel> root = query.from(NewsModel.class);
        query.select(root);

        Predicate predicates = criteriaBuilder.conjunction();

        Join<NewsModel, TagModel> newsJoinTagsByName = root.join("tagsList", JoinType.INNER);


        if (tagName != null && !tagName.isEmpty()) {
            predicates = criteriaBuilder.and(predicates, newsJoinTagsByName.get("name").in(tagName));

        }
        if (tagId != null && !tagId.isEmpty()) {
            Join<NewsModel, TagModel> newsJoinTags = root.join("tagsList");
            predicates = criteriaBuilder.and(predicates, newsJoinTags.get("id").in(tagId));

        }
        if (authorName != null) {
            Join<NewsModel, AuthorModel> newsJoinAuthor = root.join("authorModel");
            predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(newsJoinAuthor.get("name"), "%"+ authorName + "%"));
        }
        if ((title != null && !title.isEmpty()) || (content != null && !content.isEmpty())) {
            Predicate titlePredicate = null;
            Predicate contentPredicate = null;
            if (title != null && !title.isEmpty()) {
                titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
            }
            if (content != null && !content.isEmpty()) {
                contentPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content.toLowerCase() + "%");
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
query.groupBy(root.get("id"));
if(tagName != null && !tagName.isEmpty()){
        query.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.count(newsJoinTagsByName), Long.valueOf(tagName.size())));

    }

        if (sortBy != null && !sortBy.isEmpty()) {
            String[] sortByArray = sortBy.split(",");
            if (sortByArray.length == 2) {
                String sort = sortByArray[0];
                String typeSorting = sortByArray[1].equalsIgnoreCase("asc") ? "asc" : "dsc";
                if (typeSorting.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(root.get(sort)));
                } else {
                    query.orderBy(criteriaBuilder.desc(root.get(sort)));
                }
            }
        }

        TypedQuery<NewsModel> typedQuery = entityManager.createQuery(query);
        List<NewsModel> resultList = typedQuery.getResultList();
        Long totalElements= (long)resultList.size();


        List<NewsModel> newsList = entityManager.createQuery(query)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        System.out.println(newsList);

        return new NewsPageModel(newsList, totalElements);
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
