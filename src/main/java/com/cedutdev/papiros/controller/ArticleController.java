package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.ArticleDTO;
import com.cedutdev.papiros.dto.ArticleDetailDTO;
import com.cedutdev.papiros.dto.ArticleResponseDTO;
import com.cedutdev.papiros.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> listAll() {
        return ResponseEntity.ok(articleService.findAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findArticleById(id));
    }

    @PostMapping
    public ResponseEntity<ArticleResponseDTO> create(@RequestBody @Valid ArticleDTO data) {
        return ResponseEntity.ok(articleService.createArticle(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid ArticleDTO data) {
        articleService.updateArticle(id, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
