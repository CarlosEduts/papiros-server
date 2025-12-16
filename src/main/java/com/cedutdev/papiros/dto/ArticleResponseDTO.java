package com.cedutdev.papiros.dto;

public record ArticleResponseDTO(
        Long id,
        String title,
        String content,
        String author,
        String createdAt
) {
}