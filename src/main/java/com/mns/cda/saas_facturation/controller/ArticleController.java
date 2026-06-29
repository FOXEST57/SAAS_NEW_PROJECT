package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ICategoryService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
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
 * Contrôleur REST exposant les endpoints de gestion des articles.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code Article}.
 * Toutes les routes sont préfixées par {@code /article} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link IArticleService}.
 * Ce contrôleur se limite à :</p>
 * <ul>
 *   <li>recevoir les requêtes HTTP entrantes</li>
 *   <li>valider les données d'entrée via {@code @Valid}</li>
 *   <li>appeler le service approprié</li>
 *   <li>construire et retourner la {@link ResponseEntity} avec le bon statut HTTP</li>
 * </ul>
 *
 * <p>Les erreurs de validation des DTOs sont remontées au {@code GlobalExceptionInterceptor}
 * qui les transforme en réponse 400 structurée.</p>
 *
 * <p>Les exceptions métier ({@link IArticleService.ArticleNotFoundException},
 * {@link ITvaService.TvaNotFoundException}, {@link ISupplierService.SupplierNotFoundException})
 * sont propagées vers la couche de gestion globale des erreurs.</p>
 *
 * @see IArticleService
 * @see ArticleDTO
 * @see ArticleRequestDTO
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
@Tag(name = "Article", description = "Routes de gestion des articles.")
@CrossOrigin
public class ArticleController {

    /**
     * Service métier de gestion des articles, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link IArticleService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    protected final IArticleService articleService;

    /**
     * Récupère la liste complète de tous les articles enregistrés en base de données.
     *
     * <p>Les articles sont retournés sous forme de {@link ArticleDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return une {@link List} de {@link ArticleDTO} (vide si aucun article n'existe),
     *         avec le statut HTTP 200 OK implicite
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des articles.",
            description = "Cette route permet de récupérer la liste de tous les articles dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des articles récupérée avec succès.")
    })
    public List<ArticleDTO> getArticles() {
        return articleService.findAll();
    }

    /**
     * Récupère un article spécifique à partir de son identifiant unique.
     *
     * <p>Si aucun article ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param id l'identifiant unique de l'article à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link ArticleDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si l'article n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupère un article par son ID.",
            description = "Cette route permet de récupérer un article spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Article non trouvé.")
    })
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        Optional<ArticleDTO> optionalArticleDTO = articleService.findById(id);

        // Si l'Optional est vide, l'article n'existe pas en base → 404
        if (optionalArticleDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalArticleDTO.get(), HttpStatus.OK);
    }

    /**
     * Crée un nouvel article en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * <p>Le service peut lever des exceptions si la TVA ou le fournisseur référencés
     * dans le DTO n'existent pas en base de données.</p>
     *
     * @param dto les données de l'article à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link ArticleDTO} de l'article créé
     *         (avec son ID généré) et le statut HTTP 201 Created
     * @throws ITvaService.TvaNotFoundException       si le taux de TVA référencé n'existe pas
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur référencé n'existe pas
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouvel article.",
            description = "Cette route permet de créer un nouvel article dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Article créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<ArticleDTO> createArticle(
            @Valid @RequestBody ArticleRequestDTO dto
    ) throws ITvaService.TvaNotFoundException, ISupplierService.SupplierNotFoundException, ICategoryService.CategoryNotFoundException {

        ArticleDTO response = articleService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Supprime un article existant à partir de son identifiant unique.
     *
     * <p>Avant la suppression, l'existence de l'article est vérifiée. Si l'article
     * est introuvable, une réponse 404 est retournée immédiatement sans tenter
     * de suppression.</p>
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param id l'identifiant unique de l'article à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec :
     *         <ul>
     *           <li>le statut 204 No Content si la suppression a réussi</li>
     *           <li>le statut 404 Not Found si l'article n'existe pas</li>
     *         </ul>
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprime un article par son ID.",
            description = "Cette route permet de supprimer un article spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "L'article n'existe pas.")
    })
    public ResponseEntity<ArticleDTO> delete(@PathVariable Long id) {

        Optional<ArticleDTO> optionalArticle = articleService.findById(id);

        // Vérification préalable : inutile d'appeler delete() si l'article est inexistant
        if (optionalArticle.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        articleService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Met à jour intégralement un article existant à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs de l'article sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si l'article, la TVA ou le fournisseur référencés sont introuvables.</p>
     *
     * @param id  l'identifiant unique de l'article à modifier, extrait de l'URL
     * @param dto les nouvelles données de l'article, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link ArticleDTO} mis à jour
     *         avec le statut HTTP 200 OK
     * @throws IArticleService.ArticleNotFoundException   si l'article ciblé n'existe pas en base
     * @throws ITvaService.TvaNotFoundException           si le taux de TVA référencé n'existe pas
     * @throws ISupplierService.SupplierNotFoundException si le fournisseur référencé n'existe pas
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Modifie un article en base de données.",
            description = "Cette route permet de modifier un article en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "L'Article n'existe pas.")
    })
    public ResponseEntity<ArticleDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequestDTO dto
    ) throws IArticleService.ArticleNotFoundException,
            ITvaService.TvaNotFoundException,
            ISupplierService.SupplierNotFoundException, ICategoryService.CategoryNotFoundException {

        ArticleDTO updated = articleService.update(id, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}