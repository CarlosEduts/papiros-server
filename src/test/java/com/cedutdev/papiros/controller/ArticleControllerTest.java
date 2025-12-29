package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.ArticleDTO;
import com.cedutdev.papiros.dto.ArticleDetailDTO;
import com.cedutdev.papiros.dto.ArticleResponseDTO;
import com.cedutdev.papiros.infra.security.SecurityConfig;
import com.cedutdev.papiros.repository.UserRepository;
import com.cedutdev.papiros.service.ArticleService;
import com.cedutdev.papiros.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ArticleController.class)
@Import(SecurityConfig.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArticleService articleService;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Deve retornar '403' ao listar artigos sem estar autenticado")
    void listAll_ShouldReturnUnauthorized_WhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/articles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser // Simula um usuário autenticado padrão
    @DisplayName("Deve retornar '200' ao listar todos os artigos")
    void listAll_ShouldReturnOk_WhenGetAllArticlesWithSuccess() throws Exception {
        when(articleService.findAllArticles()).thenReturn(List.of());

        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar '200' ao listar artigo pelo o seu ID")
    void findById_ShouldReturnOk_WhenGetArticleByIdWithSuccess() throws Exception {
        Long articleId = 1L;
        ArticleDetailDTO response = new ArticleDetailDTO(
                articleId,
                "Título",
                "Conteúdo",
                "Autor",
                "Data de Criação",
                2L,
                List.of()
        );

        when(articleService.findArticleById(articleId)).thenReturn(response);

        mockMvc.perform(get("/articles/{id}", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.title").value(response.title()));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar '201' ao criar artigo com sucesso")
    void create_ShouldReturnCreated_WhenCreateArticleWithSuccess() throws Exception {
        ArticleDTO dto = new ArticleDTO(
                "Título",
                "Conteúdo"
        );
        ArticleResponseDTO response = new ArticleResponseDTO(
                1L,
                "Título",
                "Conteúdo",
                "Autor",
                "Data de Criação"
        );

        when(articleService.createArticle(any(ArticleDTO.class))).thenReturn(response);

        mockMvc.perform(post("/articles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.title").value(response.title()));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar '400' ao tentar criar artigo com dados inválidos")
    void create_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        ArticleDTO invalidDto = new ArticleDTO("", "");

        mockMvc.perform(post("/articles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar '204' ao atualizar artigo com sucesso")
    void update_ShouldReturnNoContent_WhenUpdateArticleWithSuccess() throws Exception {
        ArticleDTO dto = new ArticleDTO(
                "Título Atualizado",
                "Conteúdo Atualizado"
        );
        Long articleId = 1L;

        mockMvc.perform(put("/articles/{id}", articleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(articleService, times(1))
                .updateArticle(eq(articleId), any(ArticleDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar '204' ao deletar artigo com sucesso")
    void delete_ShouldReturnNoContent_WhenDeleteArticleWithSuccess() throws Exception {
        Long articleId = 1L;

        mockMvc.perform(delete("/articles/{id}", articleId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(articleService).deleteArticle(articleId);
    }
}