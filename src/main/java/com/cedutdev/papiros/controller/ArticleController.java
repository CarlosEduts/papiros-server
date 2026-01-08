package com.cedutdev.papiros.controller;

import com.cedutdev.papiros.dto.ArticleDTO;
import com.cedutdev.papiros.dto.ArticleDetailDTO;
import com.cedutdev.papiros.dto.ArticleResponseDTO;
import com.cedutdev.papiros.service.ArticleService;
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
import java.util.List;

@RestController
@RequestMapping("articles")
@RequiredArgsConstructor
@Tag(name = "Artigos", description = "Gerenciamento de artigos, apenas usuário autenticado")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "Buscar todos os artigos", description = "Este endpoint permite buscar todos os artigos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca bem sucedida"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping
    public ResponseEntity<List<ArticleResponseDTO>> listAll() {
        return ResponseEntity.ok(articleService.findAllArticles());
    }

    @Operation(summary = "Buscar artigo por ID", description = "Este endpoint permite buscar detalhes de um artigo a partir de seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca bem sucedida"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findArticleById(id));
    }

    @Operation(summary = "Criar um novo artigo", description = "Este endpoint permite criar novo artigo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Artigo com dados inválidos"),
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    public ResponseEntity<ArticleResponseDTO> create(@RequestBody @Valid ArticleDTO data) {
        ArticleResponseDTO responseDTO = articleService.createArticle(data);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Atualizar um artigo existente", description = "Este endpoint permite atualizar um artigo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artigo atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid ArticleDTO data) {
        articleService.updateArticle(id, data);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar um artigo existente", description = "Este endpoint permite deletar um artigo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artigo deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
