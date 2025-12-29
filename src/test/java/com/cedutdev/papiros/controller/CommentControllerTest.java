package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.ArticleDTO;
import com.cedutdev.papiros.dto.CommentDTO;
import com.cedutdev.papiros.dto.CommentResponseDTO;
import com.cedutdev.papiros.infra.security.SecurityConfig;
import com.cedutdev.papiros.repository.UserRepository;
import com.cedutdev.papiros.service.CommentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 201 ao criar comentário com sucesso")
    void create_ShouldReturnOk_WhenCommentIsCreated() throws Exception {
        CommentDTO dto = new CommentDTO("Conteúdo");

        CommentResponseDTO responseDTO = new CommentResponseDTO(
                1L,
                "Conteúdo",
                "Autor",
                "Data de Criação"
        );

        when(commentService.createComment(any(Long.class), any(CommentDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/articles/{articleId}/comments", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 ao tentar criar um comentário com dados inválidos")
    void create_ShouldReturnBadRequest_WhenCommentIsNotCreated() throws Exception {
        CommentDTO invalidDto = new CommentDTO("");

        mockMvc.perform(post("/articles/{articleId}/comments", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 204 ao editar um comentário com sucesso")
    void update_ShouldReturnOk_WhenAuthenticated() throws Exception {
        CommentDTO dto = new CommentDTO("Conteúdo Atualizado");

        mockMvc.perform(put("/articles/{articleId}/comments/{commentId}", 1L, 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).updateComment(eq(1L), any(CommentDTO.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 204 ao deletar artigo")
    void delete_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/articles/{articleId}/comments/{commentId}", 1L, 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(1L);
    }
}