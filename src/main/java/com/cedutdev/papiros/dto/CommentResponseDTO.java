package com.cedutdev.papiros.dto;

public record CommentResponseDTO(
        Long id,
        String content,
        String author,
        String createdAt
) {
}
