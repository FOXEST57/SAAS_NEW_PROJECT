package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.requestDTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateTvaTauxDTO;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Tva;
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
 * Contrôleur REST exposant les endpoints de gestion des taux de TVA.
 *
 * <p>Toutes les routes sont préfixées par {@code /tva} et retournent des données
 * au format JSON. Contrairement aux contrôleurs utilisant une couche DTO de sortie,
 * ce contrôleur retourne directement l'entité {@link Tva}.</p>
 *
 * <p>Ce contrôleur propose deux types de mise à jour :</p>
 * <ul>
 *   <li>{@code PUT /{id}} — remplacement complet de la TVA (tous les champs)</li>
 *   <li>{@code PATCH /{id}} — mise à jour partielle du taux uniquement</li>
 * </ul>
 *
 * <p>La logique métier est entièrement déléguée à {@link ITvaService}.
 * Les erreurs de validation (400) remontent au {@code GlobalExceptionInterceptor}.</p>
 *
 * @see ITvaService
 * @see Tva
 * @see TvaRequestDTO
 * @see UpdateTvaTauxDTO
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/tva")
@Tag(name = "Tva", description = "Routes de gestion des tva.")
@CrossOrigin
public class TvaController {

    /**
     * Service métier de gestion des TVA, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link ITvaService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    protected final ITvaService tvaService;

    /**
     * Récupère la liste complète de tous les taux de TVA enregistrés en base de données.
     * GET /tva/list
     *
     * @return une {@link List} de {@link Tva} (vide si aucune TVA n'existe),
     *         avec le statut HTTP 200 OK implicite
     */
    @GetMapping("/list")
    @Operation(summary = "Récupère la liste des TVA",
            description = "Cette route permet de récupérer la liste de toutes les tva en base de données.")
    public List<Tva> getTva() {
        return tvaService.findAll();
    }

    /**
     * Récupère un taux de TVA spécifique à partir de son identifiant unique.
     * GET /tva/{id}
     *
     * @param id l'identifiant unique de la TVA à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>la {@link Tva} correspondante avec le statut 200 OK si trouvée</li>
     *           <li>un corps vide avec le statut 404 Not Found si la TVA n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une tva par son ID.",
            description = "Cette route permet de récupérer une tva spécifique par son ID dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tva récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Tva non trouvé.")
    })
    public ResponseEntity<Tva> getTvaById(@PathVariable Long id) {
        Optional<Tva> optionalTva = tvaService.findById(id);

        // Si l'Optional est vide, la TVA n'existe pas en base → 404
        if (optionalTva.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalTva.get(), HttpStatus.OK);
    }

    /**
     * Crée un nouveau taux de TVA en base de données.
     * POST /tva
     *
     * <p>Le DTO est validé par Bean Validation ({@code @Valid}) avant d'atteindre le service.
     * En cas d'échec de validation, le {@code GlobalExceptionInterceptor} retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * @param tva les données de la TVA à créer, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant la {@link Tva} créée
     *         (avec son ID généré) et le statut HTTP 201 Created
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle tva.",
            description = "Cette route permet de créer une nouvelle tva dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tva créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<Tva> create(@Valid @RequestBody TvaRequestDTO tva) {
        Tva response = tvaService.create(tva);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Supprime un taux de TVA existant à partir de son identifiant unique.
     * DELETE /tva/{id}
     *
     * <p>L'existence de la TVA est vérifiée avant toute tentative de suppression.
     * Si elle est introuvable, une réponse 404 est retournée immédiatement.</p>
     *
     * @param id l'identifiant unique de la TVA à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec :
     *         <ul>
     *           <li>le statut 204 No Content si la suppression a réussi</li>
     *           <li>le statut 404 Not Found si la TVA n'existe pas</li>
     *         </ul>
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une tva par son ID.",
            description = "Cette route permet de supprimer une tva spécifique par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tva supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "La Tva n'existe pas.")
    })
    public ResponseEntity<Tva> delete(@PathVariable Long id) {
        Optional<Tva> optionalTva = tvaService.findById(id);

        // Vérification préalable : inutile d'appeler delete() si la TVA est inexistante
        if (optionalTva.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        tvaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Met à jour intégralement un taux de TVA existant à partir de son identifiant.
     * PUT /tva/{id}
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs de la TVA sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * @param id  l'identifiant unique de la TVA à modifier, extrait de l'URL
     * @param tva le DTO contenant les nouvelles valeurs, désérialisé depuis le corps JSON
     * @return une {@link ResponseEntity} contenant la {@link Tva} mise à jour
     *         avec le statut HTTP 200 OK
     * @throws ITvaService.TvaNotFoundException si aucune TVA ne correspond à l'id fourni
     */
    @PutMapping("/{id}")
    @Operation(summary = "Modifie une Tva en base de données.",
            description = "Cette route permet de modifier une Tva en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tva modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "La Tva n'existe pas.")
    })
    public ResponseEntity<Tva> update(
            @PathVariable Long id,
            @RequestBody TvaRequestDTO tva) throws ITvaService.TvaNotFoundException {
        Tva updatedTva = tvaService.update(id, tva);
        return new ResponseEntity<>(updatedTva, HttpStatus.OK);
    }

    /**
     * Met à jour uniquement le taux d'une TVA existante.
     * PATCH /tva/{id}
     *
     * <p>Contrairement au {@code PUT}, cette opération est une mise à jour partielle
     * (sémantique HTTP PATCH) : seul le champ {@code tvaTaux} est modifié,
     * les autres champs de l'entité restent inchangés.</p>
     *
     * <p>L'existence de la TVA est vérifiée avant toute modification.
     * Si elle est introuvable, une réponse 404 est retournée immédiatement.</p>
     *
     * @param id         l'identifiant unique de la TVA à modifier, extrait de l'URL
     * @param tvaTauxDTO le DTO contenant le nouveau taux, désérialisé depuis le corps JSON
     * @return une {@link ResponseEntity} contenant la {@link Tva} mise à jour
     *         avec le statut HTTP 200 OK
     * @throws ITvaService.TvaNotFoundException si aucune TVA ne correspond à l'id fourni
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Modifie le taux d'une Tva en base de données.",
            description = "Cette route permet de modifier le taux de Tva en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tva modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "La Tva n'existe pas.")
    })
    public ResponseEntity<Tva> patchTaux(
            @PathVariable Long id,
            UpdateTvaTauxDTO tvaTauxDTO) throws ITvaService.TvaNotFoundException {
        Optional<Tva> optionalTva = tvaService.findById(id);

        // Vérification préalable : inutile d'appeler patchTaux() si la TVA est inexistante
        if (optionalTva.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Seul le taux est transmis au service — les autres champs restent inchangés
        Tva response = tvaService.patchTaux(id, tvaTauxDTO.tvaTaux());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}