package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.LoginDTO;
import com.cedutdev.papiros.dto.RegisterDTO;
import com.cedutdev.papiros.dto.TokenDTO;
import com.cedutdev.papiros.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO data) {
        return ResponseEntity.ok(authenticationService.authenticateAndGenerateToken(data));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        boolean registerSuccess = authenticationService.registerNewUser(data);

        if (!registerSuccess) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
