package com.cedutdev.papiros.repository;

import com.cedutdev.papiros.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentByArticleId(Long articleId);
}
