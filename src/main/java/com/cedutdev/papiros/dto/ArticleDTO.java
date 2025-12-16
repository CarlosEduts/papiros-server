package com.cedutdev.papiros.dto;

import jakarta.validation.constraints.NotBlank;

public record ArticleDTO(
        @NotBlank String title,
        @NotBlank String content
) {
}
