package com.cedutdev.papiros.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.LoginDTO;
import com.cedutdev.papiros.dto.RegisterDTO;
import com.cedutdev.papiros.dto.TokenDTO;
import com.cedutdev.papiros.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("Testes de Autenticação")
    class AuthenticationTests {

        @Test
        @DisplayName("Deve retornar TokenDTO quando as credenciais forem válidas")
        void authenticateAndGenerateToken_Success() {
            // Arrange
            LoginDTO loginDTO = new LoginDTO("user", "pass123");
            User mockUser = new User("Name", "user", "pass_cripto");
            String expectedToken = "jwt-token-example";

            Authentication authMock = mock(Authentication.class);
            when(authMock.getPrincipal()).thenReturn(mockUser);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authMock);
            when(tokenService.generateToken(mockUser)).thenReturn(expectedToken);

            // Act
            TokenDTO result = authenticationService.authenticateAndGenerateToken(loginDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.token()).isEqualTo(expectedToken);
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenService).generateToken(mockUser);
        }
    }

    @Nested
    @DisplayName("Testes de Registro de Usuário")
    class RegisterTests {

        @Test
        @DisplayName("Deve retornar true e salvar usuário quando username não existir")
        void registerNewUser_Success() {
            // Arrange
            RegisterDTO registerDTO = new RegisterDTO("New User Name", "new_user", "pass123");
            when(userRepository.findByUsername(registerDTO.username())).thenReturn(null);

            // Act
            boolean result = authenticationService.registerNewUser(registerDTO);

            // Assert
            assertThat(result).isTrue();
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Deve retornar false quando o username já estiver em uso")
        void registerNewUser_Fail_AlreadyExists() {
            // Arrange
            RegisterDTO registerDTO = new RegisterDTO("Name", "existing", "pass123");
            when(userRepository.findByUsername("existing")).thenReturn(new User());

            // Act
            boolean result = authenticationService.registerNewUser(registerDTO);

            // Assert
            assertThat(result).isFalse();
            verify(userRepository, never()).save(any(User.class));
        }
    }
}