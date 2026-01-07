package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Deve retornar UserDetails quando o usuário existir")
    void loadUserByUsername_Success() {
        String username = "admin";
        User mockUser = new User();
        when(repository.findByUsername(username)).thenReturn(mockUser);

        UserDetails result = authorizationService.loadUserByUsername(username);

        assertThat(result).isEqualTo(mockUser);
        verify(repository).findByUsername(username);
    }
}