package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.CommentDTO;
import com.cedutdev.papiros.dto.CommentResponseDTO;
import com.cedutdev.papiros.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("articles")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

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

    @PutMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> update(@PathVariable Long commentId, @RequestBody @Valid CommentDTO data) {
        commentService.updateComment(commentId, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{articleId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> delete(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
