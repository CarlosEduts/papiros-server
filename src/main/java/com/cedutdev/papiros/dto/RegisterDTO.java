package com.cedutdev.papiros.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank String name,
        @NotBlank String username,
        @NotBlank String password
) {
}