package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.ArticleDTO;
import com.cedutdev.papiros.dto.ArticleDetailDTO;
import com.cedutdev.papiros.dto.ArticleResponseDTO;
import com.cedutdev.papiros.dto.CommentResponseDTO;
import com.cedutdev.papiros.repository.ArticleLikeRepository;
import com.cedutdev.papiros.repository.ArticleRepository;
import com.cedutdev.papiros.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final CommentRepository commentRepository;

    public List<ArticleResponseDTO> findAllArticles() {
        return articleRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public ArticleDetailDTO findArticleById(Long id) {
        Article article = articleRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article not found"));

        Long likes = articleLikeRepository.countByArticle(article);

        List<CommentResponseDTO> comments = commentRepository
                .findCommentByArticleId(id)
                .stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getAuthor().getUsername(),
                        comment.getCreatedAt().toString())
                ).toList();

        return new ArticleDetailDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUser().getUsername(),
                article.getCreatedAt().toString(),
                likes,
                comments
        );
    }

    public ArticleResponseDTO createArticle(ArticleDTO data) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        Article newArticle = new Article();
        newArticle.setTitle(data.title());
        newArticle.setContent(data.content());
        newArticle.setUser(user);

        articleRepository.save(newArticle);
        return mapToDTO(newArticle);
    }

    @Transactional
    public void updateArticle(Long id, ArticleDTO data) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Article articleToUpdate = articleRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article not found"));

        assert user != null;
        if (!articleToUpdate.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this article");
        }

        if (!data.title().isEmpty()) articleToUpdate.setTitle(data.title());
        if (!data.content().isEmpty()) articleToUpdate.setContent(data.content());

        articleRepository.save(articleToUpdate);
    }

    public void deleteArticle(Long id) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Article articleDelete = articleRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Article not found"));

        assert user != null;
        if (!articleDelete.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this article");
        }

        articleRepository.delete(articleDelete);
    }

    private ArticleResponseDTO mapToDTO(Article article) {
        return new ArticleResponseDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getUser().getUsername(),
                article.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
        );
    }
}
