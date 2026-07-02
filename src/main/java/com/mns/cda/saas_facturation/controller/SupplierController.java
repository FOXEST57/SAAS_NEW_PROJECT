package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
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

/**
 * Contrôleur REST exposant les endpoints de gestion des fournisseurs.
 *
 * <p>Toutes les routes sont préfixées par {@code /supplier} et retournent des données
 * au format JSON. La documentation Swagger est accessible sur {@code /swagger-ui/index.html}.</p>
 *
 * <p>Ce contrôleur orchestre deux services :</p>
 * <ul>
 *   <li>{@link ISupplierService} — pour toutes les opérations CRUD sur les fournisseurs</li>
 *   <li>{@link IArticleService} — pour récupérer les articles liés à un fournisseur donné</li>
 * </ul>
 *
 * <p>La gestion des exceptions est harmonisée avec les autres contrôleurs du projet :</p>
 * <ul>
 *   <li>Les erreurs de validation Bean (400) remontent au {@code GlobalExceptionInterceptor}</li>
 *   <li>Les doublons de nom (409) sont interceptés via la contrainte {@code unique = true}
 *       sur l'entité {@code Supplier}, remontant comme {@code DataIntegrityViolationException}
 *       vers le {@code GlobalExceptionInterceptor}</li>
 *   <li>Les 404 ({@link ISupplierService.SupplierNotFoundException}) sont déclarés avec
 *       {@code throws} et gérés localement dans les méthodes qui en ont besoin</li>
 * </ul>
 *
 * @see ISupplierService
 * @see IArticleService
 * @see SupplierDTO
 * @see SupplierRequestDTO
 */
@RestController                 // Indique à Spring que cette classe gère des requêtes HTTP et retourne du JSON
@RequiredArgsConstructor        // Lombok : génère automatiquement un constructeur avec les champs final
@CrossOrigin                    // Autorise les appels depuis d'autres domaines (ex: frontend Angular)
@RequestMapping("/supplier")    // Préfixe commun à toutes les routes de ce contrôleur
@Tag(name = "Supplier", description = "Routes de gestion des fournisseurs")
public class SupplierController {

    // Injection des services via leurs interfaces — Spring injecte automatiquement les implémentations
    protected final ISupplierService supplierService;
    private final IArticleService articleService;

    /**
     * Retourne la liste complète des fournisseurs.
     * GET /supplier/list
     *
     * @return une {@link List} de {@link SupplierDTO} (vide si aucun fournisseur n'existe),
     *         avec le statut HTTP 200 OK implicite
     */
    @GetMapping("/list")
    @Operation(summary = "Lister tous les fournisseurs", description = "Permet d'accèder à la liste des fournisseurs")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    public List<SupplierDTO> findAll() {
        return supplierService.findAll();
    }

    /**
     * Retourne un fournisseur identifié par son splId.
     * GET /supplier/{splId}
     *
     * <p>La {@link ISupplierService.SupplierNotFoundException} est interceptée localement
     * car ce contrôleur ne délègue pas les 404 au {@code GlobalExceptionInterceptor}.</p>
     *
     * @param id l'identifiant unique du fournisseur à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>le {@link SupplierDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si le fournisseur n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un fournisseur par splId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable")
    })
    public ResponseEntity<SupplierDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(supplierService.findById(id));
        } catch (ISupplierService.SupplierNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    /**
//     * Récupère la liste des articles associés à un fournisseur spécifique.
//     * GET /supplier/{splId}/articles
//     *
//     * <p>Cette route permet d'obtenir tous les articles liés à un fournisseur
//     * identifié par son splId. Si le fournisseur n'existe pas, une réponse 404 est retournée.</p>
//     *
//     * @param splId l'identifiant du fournisseur dont on veut récupérer les articles
//     * @return une {@link ResponseEntity} contenant :
//     *         <ul>
//     *           <li>200 OK avec la liste des {@link ArticleDTO} si le fournisseur existe</li>
//     *           <li>404 Not Found si aucun fournisseur ne correspond à cet splId</li>
//     *         </ul>
//     */
//    @GetMapping("/{splId}/articles")
//    @Operation(summary = "Récupérer une liste d'article par splId fournisseur")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
//            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable")
//    })
//    public ResponseEntity<List<ArticleResponseSupplierDTO>> findArticleBySupplierId(@PathVariable Long splId) {
//        try {
//            // Délègue la récupération des articles au service
//            // Le service vérifie d'abord que le fournisseur existe avant de retourner ses articles
//            return ResponseEntity.ok(articleService.findBySupplier(splId));
//        } catch (ISupplierService.SupplierNotFoundException e) {
//            // Levée par le service si aucun fournisseur ne correspond à l'splId fourni
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    /**
     * Crée un nouveau fournisseur à partir des données reçues dans le corps de la requête.
     * POST /supplier
     *
     * <p>Le DTO est validé par Bean Validation ({@code @Valid}) avant d'atteindre le service.
     * En cas d'échec de validation, le {@code GlobalExceptionInterceptor} retourne un 400.</p>
     *
     * <p>Si un fournisseur portant le même nom existe déjà, la contrainte {@code unique = true}
     * sur {@code splName} déclenche une {@code DataIntegrityViolationException}, interceptée
     * par le {@code GlobalExceptionInterceptor} qui retourne un 409 Conflict.</p>
     *
     * @param dto les données du fournisseur à créer, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant le {@link SupplierDTO} créé
     *         avec son splId généré et le statut HTTP 201 Created
     */
    @PostMapping("")
    @Operation(summary = "Créer un fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fournisseur créé"),
            @ApiResponse(responseCode = "409", description = "Fournisseur déjà existant en BDD portant le même nom")
    })
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody SupplierRequestDTO dto) throws IAddressService.AddressNotFoundException {

        try {
            // @RequestBody : Spring désérialise automatiquement le JSON reçu en SupplierRequestDTO
            SupplierDTO response = supplierService.create(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
        } catch (IAddressService.AddressNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Supprime le fournisseur correspondant à l'splId.
     * DELETE /supplier/{splId}
     *
     * @param id l'identifiant unique du fournisseur à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec :
     *         <ul>
     *           <li>le statut 204 No Content si la suppression a réussi</li>
     *           <li>le statut 404 Not Found si le fournisseur n'existe pas</li>
     *         </ul>
     * @throws ISupplierService.SupplierNotFoundException déclarée dans la signature
     *         mais interceptée localement — ne remonte jamais jusqu'au client
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fournisseur supprimé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) throws ISupplierService.SupplierNotFoundException {
        try {
            supplierService.delete(id);
            return ResponseEntity.noContent().build(); // 204 : succès sans contenu retourné
        } catch (ISupplierService.SupplierNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

    /**
     * Met à jour les informations d'un fournisseur existant.
     * PUT /supplier/modify/{splId}
     *
     * <p>Tous les champs du fournisseur sont remplacés par les valeurs fournies dans le DTO
     * (sémantique HTTP PUT — remplacement complet).</p>
     *
     * <p>Si le nouveau nom fourni est déjà utilisé par un autre fournisseur, la contrainte
     * {@code unique = true} sur {@code splName} déclenche un 409 via le
     * {@code GlobalExceptionInterceptor}.</p>
     *
     * @param id  l'identifiant unique du fournisseur à modifier, extrait de l'URL
     * @param dto le DTO contenant les nouvelles valeurs, validé par {@code @Valid}
     * @return une {@link ResponseEntity} contenant le {@link SupplierDTO} mis à jour
     *         avec le statut HTTP 200 OK
     * @throws ISupplierService.SupplierNotFoundException si aucun fournisseur ne correspond à l'splId fourni
     */
    @PutMapping("/modify/{id}")
    @Operation(summary = "Modifier un fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur modifié"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Fournisseur déjà existant en BDD portant le même nom")
    })
    public ResponseEntity<SupplierDTO> update(@PathVariable long id,
                                              @Valid
                                              @RequestBody SupplierRequestDTO dto)
            throws ISupplierService.SupplierNotFoundException,
            IAddressService.AddressNotFoundException {

        try {
            SupplierDTO updated = supplierService.modify(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IAddressService.AddressNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}