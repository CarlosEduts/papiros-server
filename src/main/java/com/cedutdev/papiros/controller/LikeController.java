package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.LikeResponseDTO;
import com.cedutdev.papiros.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{articleId}/like")
    public ResponseEntity<LikeResponseDTO> likeArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(likeService.toggleLike(articleId));
    }
}
