package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.LikeResponseDTO;
import com.cedutdev.papiros.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("articles")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "Gerenciamento de curtidas nos Artigos, apenas usuário autenticado")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Dar/Remover curtida de uma artigo", description = "Este endpoint permite dar/remover curtida de um artigo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curtido/DEscurtido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping("/{articleId}/like")
    public ResponseEntity<LikeResponseDTO> likeArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(likeService.toggleLike(articleId));
    }
}
