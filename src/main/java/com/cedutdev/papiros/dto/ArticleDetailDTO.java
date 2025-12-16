package com.cedutdev.papiros.dto;

import java.util.List;

public record ArticleDetailDTO(
        Long id,
        String title,
        String content,
        String author,
        String createdAt,
        Long likeCount,
        List<CommentResponseDTO> comments
) {
}
