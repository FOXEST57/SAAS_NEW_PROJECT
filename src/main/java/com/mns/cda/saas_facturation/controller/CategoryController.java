package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CategoryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CategoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.model.Category;
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

/**
 * Contrôleur REST exposant les endpoints de gestion des catégories d'articles.
 *
 * <p>Toutes les routes sont préfixées par {@code /category} et retournent des données
 * au format JSON. Contrairement à {@code ArticleController}, ce contrôleur retourne
 * directement l'entité {@link Category} (sans couche DTO de sortie).</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link ICategoryService}.
 * Ce contrôleur se limite à :</p>
 * <ul>
 *   <li>recevoir les requêtes HTTP entrantes</li>
 *   <li>valider les données d'entrée via {@code @Valid}</li>
 *   <li>appeler le service approprié</li>
 *   <li>construire et retourner la {@link ResponseEntity} avec le bon statut HTTP</li>
 * </ul>
 *
 * @see ICategoryService
 * @see Category
 * @see CategoryRequestDTO
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Tag(name = "Category", description = "Routes de gestion des catégories d'articles.")
@CrossOrigin
public class CategoryController {

    /**
     * Service métier de gestion des catégories, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link ICategoryService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    protected final ICategoryService categoryService;

    /**
     * Récupère la liste complète de toutes les catégories enregistrées en base de données.
     *
     * @return une {@link List} de {@link Category} (vide si aucune catégorie n'existe),
     *         avec le statut HTTP 200 OK implicite
     */
    @GetMapping("/list")
    @Operation(summary = "Récupère la liste des articles",
            description = "Cette route permet de récupérer la liste de toutes les category en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category récupéré avec succès."),
    })
    public List<CategoryDTO> getCategory() {
        return categoryService.findAll();
    }

    /**
     * Récupère une catégorie spécifique à partir de son identifiant unique.
     *
     * <p>Si aucune catégorie ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param id l'identifiant unique de la catégorie à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>la {@link Category} correspondante avec le statut 200 OK si trouvée</li>
     *           <li>un corps vide avec le statut 404 Not Found si la catégorie n'existe pas</li>
     *         </ul>
     */
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

        // Si l'Optional est vide, la catégorie n'existe pas en base → 404
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCategory.get(), HttpStatus.OK);
    }

    /**
     * Crée une nouvelle catégorie en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * @param category les données de la catégorie à créer, désérialisées depuis le corps JSON
     *                 de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant la {@link Category} créée
     *         (avec son ID généré) et le statut HTTP 201 Created
     */
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

    /**
     * Supprime une catégorie existante à partir de son identifiant unique.
     *
     * <p>Avant la suppression, l'existence de la catégorie est vérifiée. Si elle est
     * introuvable, une réponse 404 est retournée immédiatement sans tenter de suppression.</p>
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param id l'identifiant unique de la catégorie à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec :
     *         <ul>
     *           <li>le statut 204 No Content si la suppression a réussi</li>
     *           <li>le statut 404 Not Found si la catégorie n'existe pas</li>
     *         </ul>
     */
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

        // Vérification préalable : inutile d'appeler delete() si la catégorie est inexistante
        if (optionalCategory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        categoryService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Met à jour le nom d'une catégorie existante à partir de son identifiant.
     *
     * <p>Seul le champ {@code catName} du DTO est transmis au service pour la mise à jour.
     * Cette opération correspond à un remplacement complet (sémantique HTTP PUT).</p>
     *
     * @param id       l'identifiant unique de la catégorie à modifier, extrait de l'URL
     * @param category le DTO contenant le nouveau nom de la catégorie, désérialisé depuis le corps JSON
     * @return une {@link ResponseEntity} contenant la {@link Category} mise à jour
     *         avec le statut HTTP 200 OK
     * @throws ICategoryService.CategoryNotFoundException si aucune catégorie ne correspond à l'ID fourni
     */
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
