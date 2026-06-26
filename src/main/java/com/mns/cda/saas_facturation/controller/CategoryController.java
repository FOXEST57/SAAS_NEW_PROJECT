package com.mns.cda.saas_facturation.controller;


import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.DTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.UpdateTvaTauxDTO;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.model.Tva;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Tag(name = "Category", description = "Routes de gestion des articles.")
@CrossOrigin
public class CategoryController {

    protected final ICategoryService categoryService;

    @GetMapping("/list")
    @Operation(summary = "Récupère la liste des articles",
            description = "Cette route permet de récupérer la liste de toutes les category en base de données.")
    public List<CategoryDTO> getCategory()  {
        return categoryService.findAll();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Récupère une category par son ID.",
            description = "Cette route permet de récupérer une category spécifique par son ID dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Category non trouvé.")
    })
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable Long id) {
        Optional<CategoryDTO> optionalCategory = categoryService.findById(id);

        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalCategory.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle category.",
            description = "Cette route permet de créer une nouvelle category dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<CategoryDTO> create(
            @Valid @RequestBody CategoryRequestDTO category
    ) throws ICategoryService.CategoryNotFoundException {
        CategoryDTO response = categoryService.create(category);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une category par son ID.",
            description = "Cette route permet de supprimer une category spécifique par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "La Category n'existe pas.")
    })
    public ResponseEntity<CategoryDTO> delete(
            @PathVariable Long id) {

        Optional<CategoryDTO> optionalCategory = categoryService.findById(id);

        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        categoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifie une Category en base de données.",
            description = "Cette route permet de modifier une Category en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "La Category n'existe pas.")
    })
    public ResponseEntity<CategoryDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO category)
            throws ICategoryService.CategoryNotFoundException {
        CategoryDTO updatedCategory = categoryService.update(id, category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

}
