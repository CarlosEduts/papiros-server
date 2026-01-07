package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.Comment;
import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.CommentDTO;
import com.cedutdev.papiros.dto.CommentResponseDTO;
import com.cedutdev.papiros.repository.ArticleRepository;
import com.cedutdev.papiros.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentService commentService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("username");

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve criar um comentário com sucesso ")
    void createComment_Success() {
        long articleId = 1L;
        CommentDTO dto = new CommentDTO("comment content");
        Article article = new Article();
        article.setId(articleId);

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> {
            Comment c = i.getArgument(0);
            c.setId(1L);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });

        CommentResponseDTO response = commentService.createComment(articleId, dto);

        assertThat(response).isNotNull();
        assertThat(response.content()).isEqualTo(dto.content());
        assertThat(response.author()).isEqualTo(mockUser.getUsername());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando artigo não existir")
    void createComment_ArticleNotFound() {
        when(articleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(1L, new CommentDTO("...")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Article not found");
    }

    @Test
    @DisplayName("Deve atualizar quando o usuário for o autor")
    void updateComment_Success() {
        Long commentId = 1L;
        Comment existingComment = new Comment();
        existingComment.setId(commentId);
        existingComment.setAuthor(mockUser); // Mesmo usuário do SecurityContext

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        commentService.updateComment(commentId, new CommentDTO("Novo conteúdo"));
        assertThat(existingComment.getContent()).isEqualTo("Novo conteúdo");
        verify(commentRepository).save(existingComment);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando o usuário não for o autor")
    void updateComment_AccessDenied() {
        Long commentId = 1L;
        User anotherUser = new User();
        anotherUser.setId(99L); // ID diferente do mockUser (1L)

        Comment existingComment = new Comment();
        existingComment.setAuthor(anotherUser);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        assertThatThrownBy(() -> commentService.updateComment(commentId, new CommentDTO("Tentativa")))
                .isInstanceOf(AccessDeniedException.class);
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar quando o usuário for o autor")
    void deleteComment_Success() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setAuthor(mockUser);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId);
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando comentário não existir")
    void deleteComment_NotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Comment not found");
    }
}