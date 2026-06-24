package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;

import com.mns.cda.saas_facturation.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article") //permet de commencer toutes les routes de ce controller par /article
@Tag(name = "Article", description = "Routes de gestion des articles") //Doc Swagger : ajout d'un onglet Article dans la doc Swagger
@CrossOrigin
public class ArticleController {

    protected final ArticleService articleService;

    @GetMapping("/list")
    @Operation(summary = "Récupère la liste des articles",
            description = "Cette route permet de récupérer la liste de tous les articles dans la base de données.") //Doc Swagger : description de la route
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès") //Doc Swagger : Description des codes HTTP de retour
    })
    public List<ArticleDTO> getArticles() {
        return articleService.findAll();
    }

}
