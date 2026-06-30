package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CountryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CountryRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICountryService;
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
 * Contrôleur REST exposant les endpoints de gestion des pays.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code Country}.
 * Toutes les routes sont préfixées par {@code /country} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link ICountryService}.
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
 * <p>L'exception métier ({@link ICountryService.CountryNotFoundException})
 * est propagée vers la couche de gestion globale des erreurs.</p>
 *
 * @see ICountryService
 * @see CountryDTO
 * @see CountryRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/country")
@Tag(name = "Country", description = "Routes de gestion des pays.")
@RestController
@CrossOrigin
public class CountryController {

    /**
     * Service métier de gestion des pays, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link ICountryService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final ICountryService countryService;

    /**
     * Récupère la liste complète de tous les pays enregistrés en base de données.
     *
     * <p>Les pays sont retournés sous forme de {@link CountryDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return une {@link ResponseEntity} de {@link List} de {@link CountryDTO}
     * (vide si aucun pays n'existe), avec le statut HTTP 200 OK
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des pays.",
            description = "Cette route permet de récupérer la liste de tous les pays dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des pays récupérée avec succès.")
    })
    public List<CountryDTO> getCountries() {
        return countryService.findAll();
    }

    /**
     * Récupère un pays spécifique à partir de son identifiant unique.
     *
     * <p>Si aucun pays ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param cntId l'identifiant unique du pays à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link CountryDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si le pays n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{cntId}")
    @Operation(
            summary = "Récupère un pays par son ID.",
            description = "Cette route permet de récupérer un pays spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pays récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Pays non trouvé.")
    })
    public ResponseEntity<CountryDTO> getCountryById(@PathVariable Long cntId) {
        Optional<CountryDTO> optionalCountry = countryService.findById(cntId);

        if (optionalCountry.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCountry.get(), HttpStatus.OK);
    }

    /**
     * Crée un nouveau pays en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * @param dto les données du pays à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link CountryDTO} du pays créé
     *         (avec son ID généré) et le statut HTTP 201 Created
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau pays.",
            description = "Cette route permet de créer un nouveau pays dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pays créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<CountryDTO> createCountry(@RequestBody @Valid CountryRequestDTO dto) {
        CountryDTO countryCreated = countryService.create(dto);

        return new ResponseEntity<>(countryCreated, HttpStatus.CREATED);
    }

    /**
     * Met à jour intégralement un pays existant à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs du pays sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si l'article, la TVA ou le fournisseur référencés sont introuvables.</p>
     *
     * @param cntId  l'identifiant unique du pays à modifier, extrait de l'URL
     * @param dto les nouvelles données du pays, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link CountryDTO} mis à jour
     *         avec le statut HTTP 200 OK
     * @throws ICountryService.CountryNotFoundException si le pays ciblé n'existe pas en base
     */
    @PutMapping("/{cntId}")
    @Operation(
            summary = "Modifie un pays en base de données.",
            description = "Cette route permet de modifier un pays en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pays modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "Le pays n'existe pas.")
    })
    public ResponseEntity<CountryDTO> updateCountry(@PathVariable Long cntId, @RequestBody @Valid CountryRequestDTO dto) throws ICountryService.CountryNotFoundException {
        CountryDTO countryUpdated = countryService.update(cntId, dto);

        return new ResponseEntity<>(countryUpdated, HttpStatus.OK);
    }

    /**
     * Supprime un pays existant à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param cntId l'identifiant unique du pays à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi
     * @throws ICountryService.CountryNotFoundException si le pays n'existe pas en base
     */
    @DeleteMapping("/{cntId}")
    @Operation(
            summary = "Supprime un pays par son ID.",
            description = "Cette route permet de supprimer un pays spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pays supprimé avec succès.")
    })
    public ResponseEntity<Void> deleteCountry(@PathVariable Long cntId) throws ICountryService.CountryNotFoundException {
        countryService.delete(cntId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
