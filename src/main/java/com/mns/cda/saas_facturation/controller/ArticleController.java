package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;

import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.model.Article;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article") //permet de commencer toutes les routes de ce controller par /article
@Tag(name = "Article", description = "Routes de gestion des articles.") //Doc Swagger : ajout d'un onglet Article dans la doc Swagger
@CrossOrigin
public class ArticleController {

    protected final IArticleService articleService;

    @GetMapping("/list")
    @Operation(summary = "Récupère la liste des articles.",
            description = "Cette route permet de récupérer la liste de tous les articles dans la base de données.") //Doc Swagger : description de la route
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès.") //Doc Swagger : Description des codes HTTP de retour
    })
    public List<ArticleDTO> getArticles() {
        return articleService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupère un article par son ID.",
            description = "Cette route permet de récupérer un article spécifique par son ID dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Article non trouvé.")
    })
    public ResponseEntity<ArticleDTO> getArticleById(
            @PathVariable Long id) {
        Optional<ArticleDTO> optionalArticleDTO = articleService.findById(id);

        if (optionalArticleDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalArticleDTO.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Créer un nouvel article.",
            description = "Cette route permet de créer un nouvel article dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Article créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<Article> createArticle(
            @Valid @RequestBody ArticleRequestDTO dto
    ) {
        Article response = articleService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un article par son ID.",
            description = "Cette route permet de supprimer un article spécifique par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "L'article n'existe pas.")
    })
    public ResponseEntity<ArticleDTO> delete(
            @PathVariable Long id) {

        Optional<ArticleDTO> optionalArticle = articleService.findById(id);

        if (optionalArticle.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        articleService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Modifie un article en base de données.",
            description = "Cette route permet de modifier un article en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "L'Article n'existe pas.")
    })
    public ResponseEntity<ArticleDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequestDTO dto) throws IArticleService.ArticleNotFoundException {
        ArticleDTO updated = articleService.update(id, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
