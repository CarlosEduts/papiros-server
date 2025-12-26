package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.LoginDTO;
import com.cedutdev.papiros.dto.RegisterDTO;
import com.cedutdev.papiros.dto.TokenDTO;
import com.cedutdev.papiros.infra.security.SecurityConfig;
import com.cedutdev.papiros.repository.UserRepository;
import com.cedutdev.papiros.service.AuthenticationService;
import com.cedutdev.papiros.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    @DisplayName("Deve retornar 200 e TokenDTO ao fazer login com sucesso")
    void login_ShouldReturnToken_WhenDataIsValid() throws Exception {
        LoginDTO loginDTO = new LoginDTO("john-doe", "password123");
        TokenDTO tokenDTO = new TokenDTO("jwt-token-exemplo");

        when(authenticationService.authenticateAndGenerateToken(eq(loginDTO)))
                .thenReturn(tokenDTO);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-exemplo"));

        verify(authenticationService, times(1)).authenticateAndGenerateToken(any());
    }


    @Test
    @DisplayName("Deve retornar 200 ao registrar com sucesso")
    void register_Success() throws Exception {
        RegisterDTO dto = new RegisterDTO("John Doe", "john-doe", "password123");
        when(authenticationService.registerNewUser(any())).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 quando a validação do DTO falhar, name: null")
    void register_ShouldReturn400_WhenDtoIsInvalid() throws Exception {
        RegisterDTO dto = new RegisterDTO(null, "john-doe", "password");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
    }

    @Test
    @DisplayName("Deve retornar 400 quando o serviço não conseguir registrar o usuário")
    void register_ShouldReturn400_WhenServiceReturnsFalse() throws Exception {
        RegisterDTO dto = new RegisterDTO("John Doe", "john-doe", "password123");
        when(authenticationService.registerNewUser(any())).thenReturn(false); // Simula uma falha ao registrar usuário

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}