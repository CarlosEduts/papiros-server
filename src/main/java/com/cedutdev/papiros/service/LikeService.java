package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.ArticleLike;
import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.LikeResponseDTO;
import com.cedutdev.papiros.repository.LikeRepository;
import com.cedutdev.papiros.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;

    public LikeResponseDTO toggleLike(Long articleId) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new RuntimeException("Article not found"));
        var existingLike = likeRepository.findByArticleAndUser(article, user);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return new LikeResponseDTO(false);
        } else {
            likeRepository.save(new ArticleLike(article, user));
            return new LikeResponseDTO(true);
        }
    }
}
