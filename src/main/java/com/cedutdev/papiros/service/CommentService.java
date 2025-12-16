package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.Comment;
import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.CommentDTO;
import com.cedutdev.papiros.dto.CommentResponseDTO;
import com.cedutdev.papiros.repository.ArticleRepository;
import com.cedutdev.papiros.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    public CommentResponseDTO createComment(Long articleId, CommentDTO data){
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new EntityNotFoundException("Article not found"));

        Comment comment = new Comment();
        comment.setContent(data.content());
        comment.setArticle(article);
        comment.setAuthor(user);

        commentRepository.save(comment);

        assert user != null;
        return mapToDTO(comment, user);
    }

    public void updateComment(Long commentId, CommentDTO data){
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Comment commentToUpdate = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));

        assert user != null;
        if (!commentToUpdate.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this comment");
        }

        if (!data.content().isEmpty()) commentToUpdate.setContent(data.content());

        commentRepository.save(commentToUpdate);
    }

    public void deleteComment(Long commentId){
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Comment commentToDelete = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment not found"));

        assert user != null;
        if (!commentToDelete.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this comment");
        }

        commentRepository.delete(commentToDelete);
    }

    private CommentResponseDTO mapToDTO(Comment comment, User user){
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                user.getUsername(),
                comment.getCreatedAt().toString()
        );
    }
}
