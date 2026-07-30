package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.InventoryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.InventoryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IInventoryService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
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
 * Contrôleur REST exposant les endpoints de gestion des villes.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code Inventory}.
 * Toutes les routes sont préfixées par {@code /inventory} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link IInventoryService}.
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
 * <p>L'exception ({@link ResourceNotFoundException} est propagée vers la couche de gestion globale des erreurs.</p>
 *
 * @see IInventoryService
 * @see InventoryDTO
 * @see InventoryRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Routes de gestion des villes.")
@RestController
@CrossOrigin
public class InventoryController {

    /**
     * Service métier de gestion des villes, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link IInventoryService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final IInventoryService inventoryService;

    /**
     * Récupère la liste complète de toutes les villes enregistrées en base de données.
     *
     * <p>Les villes sont retournées sous forme de {@link InventoryDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return la {@link List<InventoryDTO>} correspondant avec le statut 200 OK si trouvé
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des villes.",
            description = "Cette route permet de récupérer la liste de toutes les villes dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des villes récupérée avec succès.")
    })
    public List<InventoryDTO> getInventories() {
        return inventoryService.findAll();
    }

    /**
     * Récupère une ville spécifique à partir de son identifiant unique.
     *
     * <p>Si aucune ville ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param invId l'identifiant unique de la ville à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link InventoryDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si la ville n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{invId}")
    @Operation(
            summary = "Récupère une ville par son ID.",
            description = "Cette route permet de récupérer une ville spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée.")
    })
    public ResponseEntity<InventoryDTO> getInventoryById(@PathVariable Long invId) {
        Optional<InventoryDTO> optionalInventory = inventoryService.findById(invId);

        if (optionalInventory.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalInventory.get(), HttpStatus.OK);
    }

    /**
     * Crée une nouvelle ville en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * <p>Le service peut lever des exceptions si le pays référencé
     * dans le DTO n'existe pas en base de données.</p>
     *
     * @param dto les données de la ville à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} vide avec le statut HTTP 201 Created
     * @throws ResourceNotFoundException si le pays référencé n'existe pas
     */
    @PostMapping()
    @Operation(
            summary = "Créer une nouvelle ville.",
            description = "Cette route permet de créer une nouvelle ville dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ville créée avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<InventoryDTO> createInventory(@RequestBody @Valid InventoryRequestDTO dto) {
        InventoryDTO inventoryCreated = inventoryService.create(dto);

        return new ResponseEntity<>(inventoryCreated, HttpStatus.CREATED);
    }

    /**
     * Met à jour intégralement une ville existante à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs de la ville sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si la ville ou le pays référencé sont introuvables.</p>
     *
     * @param invId  l'identifiant unique de la ville à modifier, extrait de l'URL
     * @param dto les nouvelles données de la ville, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant la {@link InventoryDTO} mise à jour
     *         avec le statut HTTP 200 OK
     * @throws ResourceNotFoundException si la ville ciblée ou le pays référencé n'existe pas
     */
    @PutMapping("/{invId}")
    @Operation(
            summary = "Modifie une ville en base de données.",
            description = "Cette route permet de modifier une ville en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville modifiée avec succès."),
            @ApiResponse(responseCode = "404", description = "La ville n'existe pas.")
    })
    public ResponseEntity<InventoryDTO> updateInventory(@PathVariable Long invId, @RequestBody @Valid InventoryRequestDTO dto) {
        InventoryDTO inventoryUpdated = inventoryService.update(invId, dto);

        return new ResponseEntity<>(inventoryUpdated, HttpStatus.OK);
    }

    /**
     * Supprime une ville existante à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param invId l'identifiant unique de la ville à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi</li>
     * @throws ResourceNotFoundException si la ville ciblée n'existe pas en base
     */
    @DeleteMapping("/{invId}")
    @Operation(
            summary = "Supprime une ville par son ID.",
            description = "Cette route permet de supprimer une ville spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article supprimé avec succès.")
    })
    public ResponseEntity<Void> deleteInventory(@PathVariable Long invId) {
        inventoryService.delete(invId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
