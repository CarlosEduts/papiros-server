package com.cedutdev.papiros.repository;

import com.cedutdev.papiros.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
