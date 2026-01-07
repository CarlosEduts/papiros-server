package com.cedutdev.papiros.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.ArticleLike;
import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.LikeResponseDTO;
import com.cedutdev.papiros.repository.ArticleRepository;
import com.cedutdev.papiros.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private LikeService likeService;

    private User mockUser;
    private Article mockArticle;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);

        mockArticle = new Article();
        mockArticle.setId(mockArticle.getId());

        // Mock do SecurityContext estático
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve dar LIKE (salvar) quando o usuário ainda não curtiu o artigo")
    void toggleLike_ShouldCreateLike_WhenNotPresent() {
        when(articleRepository.findById(mockArticle.getId())).thenReturn(Optional.of(mockArticle));
        when(likeRepository.findByArticleAndUser(mockArticle, mockUser)).thenReturn(Optional.empty());

        LikeResponseDTO response = likeService.toggleLike(mockArticle.getId());

        assertThat(response.isLiked()).isTrue();
        verify(likeRepository).save(any(ArticleLike.class));
        verify(likeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve dar UNLIKE (deletar) quando o usuário já curtiu o artigo")
    void toggleLike_ShouldRemoveLike_WhenAlreadyPresent() {
        ArticleLike existingLike = new ArticleLike(mockArticle, mockUser);
        when(articleRepository.findById(mockArticle.getId())).thenReturn(Optional.of(mockArticle));
        when(likeRepository.findByArticleAndUser(mockArticle, mockUser)).thenReturn(Optional.of(existingLike));

        LikeResponseDTO response = likeService.toggleLike(mockArticle.getId());

        assertThat(response.isLiked()).isFalse();
        verify(likeRepository).delete(existingLike);
        verify(likeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o artigo não for encontrado")
    void toggleLike_ShouldThrowException_WhenArticleNotFound() {
        when(articleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.toggleLike(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Article not found");

        verifyNoInteractions(likeRepository);
    }
}