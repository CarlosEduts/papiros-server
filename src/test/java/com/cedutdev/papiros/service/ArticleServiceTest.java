package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.Article;
import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.ArticleDTO;
import com.cedutdev.papiros.dto.ArticleDetailDTO;
import com.cedutdev.papiros.dto.ArticleResponseDTO;
import com.cedutdev.papiros.repository.ArticleRepository;
import com.cedutdev.papiros.repository.CommentRepository;
import com.cedutdev.papiros.repository.LikeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ArticleService articleService;

    private User mockUser;
    private Article mockArticle;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("username");

        mockArticle = new Article();
        mockArticle.setId(1L);
        mockArticle.setTitle("title");
        mockArticle.setContent("content");
        mockArticle.setCreatedAt(LocalDateTime.now());
        mockArticle.setUser(mockUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(User principal) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve retornar a lista de ArticleResponseDTO quando os artigos existirem")
    void findAllArticles_ShouldReturnList() {
        when(articleRepository.findAll()).thenReturn(List.of(mockArticle));

        List<ArticleResponseDTO> result = articleService.findAllArticles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo(mockArticle.getTitle());
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar ArticleDetailDTO quando consultado um artigo pelo seu id")
    void findArticleById_ShouldReturnArticleDetailDTO() {
        Long articleId = 1L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(mockArticle));
        when(likeRepository.countByArticle(mockArticle)).thenReturn(5L);
        when(commentRepository.findCommentByArticleId(articleId)).thenReturn(List.of());

        ArticleDetailDTO result = articleService.findArticleById(articleId);

        assertThat(result.id()).isEqualTo(articleId);
        assertThat(result.title()).isEqualTo(mockArticle.getTitle());
        assertThat(result.likeCount()).isEqualTo(5L);
        assertThat(result.author()).isEqualTo(mockArticle.getUser().getUsername());
    }

    @Test
    @DisplayName("Deve retornar ResponseStatusException quando o id do artigo não for encontrado")
    void findArticleById_ShouldThrowException() {
        Long articleId = 100L;
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.findArticleById(articleId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Article not found")
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Deve criar artigo com sucesso")
    void saveArticle_ShouldSaveArticleWithSuccess() {
        mockSecurityContext(mockUser);
        ArticleDTO dto = new ArticleDTO("New Title", "New Content");

        when(articleRepository.save(any(Article.class))).thenAnswer(i -> {
            Article saved = i.getArgument(0);
            saved.setId(99L); // Simula id gerado pelo database
            saved.setCreatedAt(LocalDateTime.now());
            return saved;
        });

        ArticleResponseDTO result = articleService.createArticle(dto);

        assertThat(result.title()).isEqualTo("New Title");
        assertThat(result.author()).isEqualTo("username");

        ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepository).save(articleCaptor.capture());
        Article captured = articleCaptor.getValue();
        assertThat(captured.getUser()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("Deve atualizar o artigo quando o usuário tiver permissão/for o mesmo")
    void updateArticle_ShouldUpdateArticleWithSuccess() {
        mockSecurityContext(mockUser);
        ArticleDTO dto = new ArticleDTO("Updated Title", "Updated Content");

        when(articleRepository.findById(10L)).thenReturn(Optional.of(mockArticle));

        articleService.updateArticle(10L, dto);

        verify(articleRepository).save(mockArticle);
        assertThat(mockArticle.getTitle()).isEqualTo("Updated Title");
        assertThat(mockArticle.getContent()).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("Deve retornar AccessDeniedException quando o usuário não tiver permissão/for o mesmo")
    void updateArticle_AccessDenied() {
        User invader = new User();
        invader.setId(2L); // ID diferente do dono do artigo (1L)
        mockSecurityContext(invader);

        when(articleRepository.findById(10L)).thenReturn(Optional.of(mockArticle));

        ArticleDTO updateDto = new ArticleDTO("Title", "Content");
        assertThatThrownBy(() -> articleService.updateArticle(10L, updateDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to modify this article");

        verify(articleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar o artigo quando o usuário tiver permissão")
    void deleteArticle_Success() {
        mockSecurityContext(mockUser);
        when(articleRepository.findById(10L)).thenReturn(Optional.of(mockArticle));

        articleService.deleteArticle(10L);

        verify(articleRepository).delete(mockArticle);
    }

    @Test
    @DisplayName("Deve retornar AccessDeniedException quando o usuário não tiver permissão para deletar o artigo")
    void deleteArticle_AccessDenied() {
        User invader = new User();
        invader.setId(2L);
        mockSecurityContext(invader);

        when(articleRepository.findById(10L)).thenReturn(Optional.of(mockArticle));

        assertThatThrownBy(() -> articleService.deleteArticle(10L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("You do not have permission to modify this article");

        verify(articleRepository, never()).save(any());
    }
}