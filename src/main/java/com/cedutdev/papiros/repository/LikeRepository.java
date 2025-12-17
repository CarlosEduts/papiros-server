package com.cedutdev.papiros.repository;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.ArticleLike;
import com.cedutdev.papiros.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleAndUser(Article article, User user);

    Long countByArticle(Article article);
}
