package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.*;
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
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code City}.
 * Toutes les routes sont préfixées par {@code /city} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link ICityService}.
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
 * <p>Les exceptions métier ({@link ICityService.CityNotFoundException},
 * {@link ICountryService.CountryNotFoundException})
 * sont propagées vers la couche de gestion globale des erreurs.</p>
 *
 * @see ICityService
 * @see CityDTO
 * @see CityRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/city")
@Tag(name = "City", description = "Routes de gestion des villes.")
@RestController
@CrossOrigin
public class CityController {

    /**
     * Service métier de gestion des villes, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link ICityService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final ICityService cityService;

    /**
     * Récupère la liste complète de toutes les villes enregistrées en base de données.
     *
     * <p>Les villes sont retournées sous forme de {@link CityDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return la {@link List<CityDTO>} correspondant avec le statut 200 OK si trouvé
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des villes.",
            description = "Cette route permet de récupérer la liste de toutes les villes dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des villes récupérée avec succès.")
    })
    public List<CityDTO> getCities() {
        return cityService.findAll();
    }

    /**
     * Récupère une ville spécifique à partir de son identifiant unique.
     *
     * <p>Si aucune ville ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param cityId l'identifiant unique de la ville à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link CityDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si la ville n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{cityId}")
    @Operation(
            summary = "Récupère une ville par son ID.",
            description = "Cette route permet de récupérer une ville spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée.")
    })
    public ResponseEntity<CityDTO> getCityById(@PathVariable Long cityId) {
        Optional<CityDTO> optionalCity = cityService.findById(cityId);

        if (optionalCity.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCity.get(), HttpStatus.OK);
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
     * @throws ICountryService.CountryNotFoundException si le pays référencé n'existe pas
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
    public ResponseEntity<CityDTO> createCity(@RequestBody @Valid CityRequestDTO dto) throws ICountryService.CountryNotFoundException {
        CityDTO cityCreated = cityService.create(dto);

        return new ResponseEntity<>(cityCreated, HttpStatus.CREATED);
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
     * @param cityId  l'identifiant unique de la ville à modifier, extrait de l'URL
     * @param dto les nouvelles données de la ville, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant la {@link CityDTO} mise à jour
     *         avec le statut HTTP 200 OK
     * @throws ICityService.CityNotFoundException             si la ville ciblée n'existe pas en base
     * @throws ICountryService.CountryNotFoundException si le pays référencé n'existe pas
     */
    @PutMapping("/{cityId}")
    @Operation(
            summary = "Modifie une ville en base de données.",
            description = "Cette route permet de modifier une ville en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ville modifiée avec succès."),
            @ApiResponse(responseCode = "404", description = "La ville n'existe pas.")
    })
    public ResponseEntity<CityDTO> updateCity(@PathVariable Long cityId, @RequestBody @Valid CityRequestDTO dto) throws ICityService.CityNotFoundException, ICountryService.CountryNotFoundException {
        CityDTO cityUpdated = cityService.update(cityId, dto);

        return new ResponseEntity<>(cityUpdated, HttpStatus.OK);
    }

    /**
     * Supprime une ville existante à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param cityId l'identifiant unique de la ville à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi</li>
     * @throws ICityService.CityNotFoundException si la ville ciblée n'existe pas en base
     */
    @DeleteMapping("/{cityId}")
    @Operation(
            summary = "Supprime une ville par son ID.",
            description = "Cette route permet de supprimer une ville spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article supprimé avec succès.")
    })
    public ResponseEntity<Void> deleteCity(@PathVariable Long cityId) throws ICityService.CityNotFoundException {
        cityService.delete(cityId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
