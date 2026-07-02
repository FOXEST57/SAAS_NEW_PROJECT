package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
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
 * Contrôleur REST exposant les endpoints de gestion des codes postaux.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code PostalCode}.
 * Toutes les routes sont préfixées par {@code /postalcode} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link IPostalCodeService}.
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
 * <p>L'exception ({@link ResourceNotFoundException}) est propagée vers la couche de gestion globale des erreurs.</p>
 *
 * @see IPostalCodeService
 * @see PostalCodeDTO
 * @see PostalCodeRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/postalcode")
@Tag(name = "PostalCode", description = "Routes de gestion des codes postaux.")
@RestController
@CrossOrigin
public class PostalCodeController {

    /**
     * Service métier de gestion des codes postaux, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link IPostalCodeService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final IPostalCodeService postalCodeService;

    /**
     * Récupère la liste complète de tous les codes postaux enregistrés en base de données.
     *
     * <p>Les codes postaux sont retournés sous forme de {@link PostalCodeDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return une {@link ResponseEntity} de {@link List} de {@link PostalCodeDTO}
     * (vide si aucun code postal n'existe), avec le statut HTTP 200 OK
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des codes postaux.",
            description = "Cette route permet de récupérer la liste de tous les codes postaux dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des codes postaux récupérée avec succès.")
    })
    public List<PostalCodeDTO> getPostalCodes() {
        return postalCodeService.findAll();
    }

    /**
     * Récupère un code postal spécifique à partir de son identifiant unique.
     *
     * <p>Si aucun code postal ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param pcodeId l'identifiant unique du code postal à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link PostalCodeDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si le code postal n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{pcodeId}")
    @Operation(
            summary = "Récupère un code postal par son ID.",
            description = "Cette route permet de récupérer un code postal spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code postal récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Code postal non trouvé.")
    })
    public ResponseEntity<PostalCodeDTO> getPostalCodeById(@PathVariable Long pcodeId) {
        Optional<PostalCodeDTO> optionalPostalCode = postalCodeService.findById(pcodeId);

        if (optionalPostalCode.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalPostalCode.get(), HttpStatus.OK);
    }

    /**
     * Crée un nouveau code postal en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * @param dto les données du code postal à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link PostalCodeDTO} du code postal créé
     *         (avec son ID généré) et le statut HTTP 201 Created
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau code postal.",
            description = "Cette route permet de créer un nouveau code postal dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Code postal créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<PostalCodeDTO> createPostalCode(@RequestBody @Valid PostalCodeRequestDTO dto) {
        PostalCodeDTO postalCodeCreated = postalCodeService.create(dto);

        return new ResponseEntity<>(postalCodeCreated, HttpStatus.CREATED);
    }

    /**
     * Met à jour intégralement un code postal existant à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs du code postal sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si l'article, la TVA ou le fournisseur référencés sont introuvables.</p>
     *
     * @param pcodeId  l'identifiant unique du code postal à modifier, extrait de l'URL
     * @param dto les nouvelles données du code postal, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link PostalCodeDTO} mis à jour
     *         avec le statut HTTP 200 OK
     * @throws ResourceNotFoundException si le code postal ciblé n'existe pas en base
     */
    @PutMapping("/{pcodeId}")
    @Operation(
            summary = "Modifie un code postal en base de données.",
            description = "Cette route permet de modifier un code postal en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code postal modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "Le code postal n'existe pas.")
    })
    public ResponseEntity<PostalCodeDTO> updatePostalCode(@PathVariable Long pcodeId, @RequestBody @Valid PostalCodeRequestDTO dto) {
        PostalCodeDTO postalCodeUpdated = postalCodeService.update(pcodeId, dto);

        return new ResponseEntity<>(postalCodeUpdated, HttpStatus.OK);
    }

    /**
     * Supprime un code postal existant à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param pcodeId l'identifiant unique du code postal à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi
     * @throws ResourceNotFoundException si le code postal n'existe pas en base
     */
    @DeleteMapping("/{pcodeId}")
    @Operation(
            summary = "Supprime un code postal par son ID.",
            description = "Cette route permet de supprimer un code postal spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Code postal supprimé avec succès.")
    })
    public ResponseEntity<Void> deletePostalCode(@PathVariable Long pcodeId) {
        postalCodeService.delete(pcodeId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
