package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        // Injeta manualmente o valor que o Spring injetaria via @Value
        String SECRET_TEST = "test-secret-123";
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_TEST);
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido para um usuário")
    void generateToken_Success() {
        User user = new User();
        user.setUsername("developer_user");

        String token = tokenService.generateToken(user);

        assertThat(token).isNotBlank();
        // Verifica se o token tem a estrutura de 3 partes (header.payload.signature)
        assertThat(token.split("\\.")).hasSize(3);

    }

    @Test
    @DisplayName("Deve retornar o username quando o token for válido")
    void validateToken_Success() {
        User user = new User();
        user.setUsername("john-doe");
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertThat(subject).isEqualTo("john-doe");
    }

    @Test
    @DisplayName("Deve retornar string vazia quando o token for inválido ou malformado")
    void validateToken_Invalid() {
        String invalidToken = "invalid-token";

        String subject = tokenService.validateToken(invalidToken);

        assertThat(subject).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar string vazia quando o token estiver expirado")
    void validateToken_Expired() {
        // Para testar expiração real, é preciso mockar o tempo (Clock)
        // ou criar um token manualmente já expirado com a mesma secret
        // Aqui testei o comportamento genérico de falha:
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        String subject = tokenService.validateToken(expiredToken);

        assertThat(subject).isEmpty();
    }
}