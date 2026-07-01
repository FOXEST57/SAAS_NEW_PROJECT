package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeCityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.model.PostalCodeCity;
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
 * Contrôleur REST exposant les endpoints de gestion des liens entre code postal et ville.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code PostalCodeCity}.
 * Toutes les routes sont préfixées par {@code /postalcodecity} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link IPostalCodeCityService}.
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
 * <p>L'exception métier ({@link IPostalCodeCityService.PostalCodeCityNotFoundException})
 * est propagée vers la couche de gestion globale des erreurs.</p>
 *
 * @see IPostalCodeCityService
 * @see PostalCodeCityRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/postalcodecity")
@Tag(name = "PostalCodeCity", description = "Routes de gestion des liens entre code postal et ville.")
@RestController
@CrossOrigin
public class PostalCodeCityController {

    /**
     * Service métier de gestion des liens entre code postal et ville, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link IPostalCodeCityService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final IPostalCodeCityService postalCodeCityService;

    /**
     * Récupère la liste complète de tous les liens entre code postal et ville enregistrés en base de données.
     *
     * <p>Les liens entre code postal et ville sont retournés sous forme de {pcodeId, cityId}
     *
     * @return une {@link ResponseEntity} de {@link List} de {pcodeId, cityId}
     * (vide si aucun lien entre code postal et ville n'existe), avec le statut HTTP 200 OK
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des liens entre code postal et ville.",
            description = "Cette route permet de récupérer la liste de tous les liens entre code postal et ville dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des liens entre code postal et ville récupérée avec succès.")
    })
    public List<PostalCodeCity> getPostalCodeCities() {
        return postalCodeCityService.findAll();
    }

    /**
     * Récupère un liens entre code postal et ville spécifique à partir de son identifiant unique.
     *
     * <p>Si aucun liens entre code postal et ville ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param pcodeId l'identifiant unique du code postal à récupérer, extrait de l'URL
     * @param cityId l'identifiant unique de la ville à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>la réponse correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si le lien entre code postal et ville n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{pcodeId}/{cityId}")
    @Operation(
            summary = "Récupère un lien entre code postal et ville par son ID.",
            description = "Cette route permet de récupérer un lien entre code postal et ville spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lien entre code postal et ville récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Lien entre code postal et ville non trouvé.")
    })
    public ResponseEntity<PostalCodeCity> getPostalCodeCityById(@PathVariable Long pcodeId, @PathVariable Long cityId) {
        PostalCodeCity.PostalCodeCityId id = new PostalCodeCity.PostalCodeCityId(pcodeId, cityId);
        Optional<PostalCodeCity> optionalPostalCodeCity = postalCodeCityService.findById(id);

        if (optionalPostalCodeCity.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalPostalCodeCity.get(), HttpStatus.OK);
    }

    /**
     * Crée un nouveau lien entre code postal et ville en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * @param dto les données du lien entre code postal et ville à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant les identifiants du lien entre code postal et ville créé
     *         (avec son ID généré) et le statut HTTP 201 Created
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau lien entre code postal et ville.",
            description = "Cette route permet de créer un nouveau lien entre code postal et ville dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lien entre code postal et ville créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<PostalCodeCity> createPostalCodeCity(@RequestBody @Valid PostalCodeCityRequestDTO dto) throws IPostalCodeCityService.PostalCodeCityNotFoundException, ICityService.CityNotFoundException, IPostalCodeService.PostalCodeNotFoundException {
        PostalCodeCity postalCodeCityCreated = postalCodeCityService.create(dto);

        return new ResponseEntity<>(postalCodeCityCreated, HttpStatus.CREATED);
    }

    /**
     * Met à jour intégralement un lien entre code postal et ville existant à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs du lien entre code postal et ville sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si l'article, la TVA ou le fournisseur référencés sont introuvables.</p>
     *
     * @param pcodeId l'identifiant unique du code postal à modifier, extrait de l'URL
     * @param cityId l'identifiant unique de la ville à modifier, extrait de l'URL
     * @param dto les nouvelles données du lien entre code postal et ville, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant les données mises à jour
     *         avec le statut HTTP 200 OK
     * @throws IPostalCodeCityService.PostalCodeCityNotFoundException si le lien entre code postal et ville ciblé n'existe pas en base
     */
    @PutMapping("/{pcodeId}/{cityId}")
    @Operation(
            summary = "Modifie un lien entre code postal et ville en base de données.",
            description = "Cette route permet de modifier un lien entre code postal et ville en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lien entre code postal et ville modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "Le lien entre code postal et ville n'existe pas.")
    })
    public ResponseEntity<PostalCodeCity> updatePostalCodeCity(@PathVariable Long pcodeId, @PathVariable Long cityId, @RequestBody @Valid PostalCodeCityRequestDTO dto) throws IPostalCodeCityService.PostalCodeCityNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {
        PostalCodeCity.PostalCodeCityId id = new PostalCodeCity.PostalCodeCityId(pcodeId, cityId);
        PostalCodeCity postalCodeCityUpdated = postalCodeCityService.update(id, dto);

        return new ResponseEntity<>(postalCodeCityUpdated, HttpStatus.OK);
    }

    /**
     * Supprime un lien entre code postal et ville existant à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param pcodeId l'identifiant unique code postal à supprimer, extrait de l'URL
     * @param cityId l'identifiant unique de la ville à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi
     * @throws IPostalCodeCityService.PostalCodeCityNotFoundException si le lien entre code postal et ville n'existe pas en base
     */
    @DeleteMapping("/{pcodeId}/{cityId}")
    @Operation(
            summary = "Supprime un lien entre code postal et ville par son ID.",
            description = "Cette route permet de supprimer un lien entre code postal et ville spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lien entre code postal et ville supprimé avec succès.")
    })
    public ResponseEntity<Void> deletePostalCodeCity(@PathVariable Long pcodeId, @PathVariable Long cityId) throws IPostalCodeCityService.PostalCodeCityNotFoundException {
        PostalCodeCity.PostalCodeCityId id = new PostalCodeCity.PostalCodeCityId(pcodeId, cityId);
        postalCodeCityService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
