package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.CommentDTO;
import com.cedutdev.papiros.dto.CommentResponseDTO;
import com.cedutdev.papiros.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("articles")
@RequiredArgsConstructor
@Tag(name = "Comentários", description = "Gerenciamento de comentários, apenas usuário autenticado")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Criar um novo comentário", description = "Este endpoint permite criar novo comentário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Comentário com dados inválidos"),
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping("/{articleId}/comments")
    public ResponseEntity<CommentResponseDTO> create(@PathVariable Long articleId, @RequestBody @Valid CommentDTO data) {
        CommentResponseDTO responseDTO = commentService.createComment(articleId, data);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{commentId}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Atualizar um comentário existente", description = "Este endpoint permite atualizar um comentário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentário atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable Long commentId, @RequestBody @Valid CommentDTO data) {
        commentService.updateComment(commentId, data);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar um comentário existente", description = "Este endpoint permite deletar um comentário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentário deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> delete(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
