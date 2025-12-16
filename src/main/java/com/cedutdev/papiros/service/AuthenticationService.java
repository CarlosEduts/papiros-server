package com.cedutdev.papiros.service;

import com.cedutdev.papiros.domain.User;
import com.cedutdev.papiros.dto.LoginDTO;
import com.cedutdev.papiros.dto.RegisterDTO;
import com.cedutdev.papiros.dto.TokenDTO;
import com.cedutdev.papiros.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public TokenDTO authenticateAndGenerateToken(LoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));

        return new TokenDTO(token);
    }

    public boolean registerNewUser(RegisterDTO data) {
        if (userRepository.findByUsername(data.username()) != null) {
            return false;
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.username(), encryptedPassword);

        userRepository.save(newUser);
        return true;
    }
}
