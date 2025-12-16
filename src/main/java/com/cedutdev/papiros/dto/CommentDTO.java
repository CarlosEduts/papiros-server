package com.cedutdev.papiros.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentDTO(@NotBlank String content) {
}
