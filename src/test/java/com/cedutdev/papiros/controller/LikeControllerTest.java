package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.LikeResponseDTO;
import com.cedutdev.papiros.infra.security.SecurityConfig;
import com.cedutdev.papiros.repository.UserRepository;
import com.cedutdev.papiros.service.LikeService;
import com.cedutdev.papiros.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@Import(SecurityConfig.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LikeService likeService;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("Deve garantir que o ID da URL é repassado corretamente para o serviço")
    void likeArticle_ShouldPassCorrectIdToService() throws Exception {
        Long articleId = 123L;
        when(likeService.toggleLike(articleId)).thenReturn(new LikeResponseDTO(true));

        mockMvc.perform(post("/articles/{id}/like", articleId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(likeService, times(1)).toggleLike(articleId);
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 200 e 'true' ao dar like em um artigo com sucesso")
    void likeArticle_ShouldReturnOkAndTrue_WhenAuthenticated() throws Exception {
        LikeResponseDTO likeResponseDTO = new LikeResponseDTO(true);

        when(likeService.toggleLike(1L)).thenReturn(likeResponseDTO);

        mockMvc.perform(post("/articles/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLiked").value(likeResponseDTO.isLiked()));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 200 e 'false' ao remover o like em um artigo com sucesso")
    void likeArticle_ShouldReturnOkAndFalse_WhenAuthenticated() throws Exception {
        LikeResponseDTO likeResponseDTO = new LikeResponseDTO(false);

        when(likeService.toggleLike(1L)).thenReturn(likeResponseDTO);

        mockMvc.perform(post("/articles/1/like").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLiked").value(likeResponseDTO.isLiked()));
    }

    @Test
    @DisplayName("Deve retornar 403 se o usuário não estiver autenticado")
    void likeArticle_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/articles/1/like").with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(likeService);
    }
}