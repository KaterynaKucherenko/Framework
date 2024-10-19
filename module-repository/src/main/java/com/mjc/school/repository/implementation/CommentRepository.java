package com.mjc.school.repository.implementation;

import com.mjc.school.repository.model.CommentModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("commentRepository")
public class CommentRepository extends AbstractDBRepository<CommentModel, Long> {

    public List<CommentModel> readListOfCommentsByNewsId(Long newsId) {
        List<CommentModel> result = entityManager.createQuery("SELECT a FROM CommentModel a INNER JOIN a.newsModel b WHERE b.id=:newsId", CommentModel.class).setParameter("newsId", newsId).getResultList();
        return result;
    }

    @Override
    void update(CommentModel prevState, CommentModel nextState) {
        if (nextState.getContent() != null && !nextState.getContent().isBlank()) {
            prevState.setContent(nextState.getContent());
        }

    }


}
