package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.AddressDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
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
 * Contrôleur REST exposant les endpoints de gestion des adresses.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code Address}.
 * Toutes les routes sont préfixées par {@code /address} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link IAddressService}.
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
 * <p>Les exceptions métier ({@link IAddressService.AddressNotFoundException},
 * {@link IPostalCodeService.PostalCodeNotFoundException}, {@link ICityService.CityNotFoundException})
 * sont propagées vers la couche de gestion globale des erreurs.</p>
 *
 * @see IAddressService
 * @see AddressDTO
 * @see AddressRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/address")
@Tag(name = "Address", description = "Routes de gestion des adresses.")
@RestController
@CrossOrigin
public class AddressController {

    /**
     * Service métier de gestion des adresses, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link IAddressService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final IAddressService addressService;

    /**
     * Récupère la liste complète de toutes les adresses enregistrées en base de données.
     *
     * <p>Les adresses sont retournées sous forme de {@link AddressDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return la {@link List<AddressDTO>} correspondant avec le statut 200 OK si trouvé
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des adresses.",
            description = "Cette route permet de récupérer la liste de toutes les adresses dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des adresses récupérée avec succès.")
    })
    public List<AddressDTO> getAddresses() {
        return addressService.findAll();
    }

    /**
     * Récupère une adresse spécifique à partir de son identifiant unique.
     *
     * <p>Si aucune adresse ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param addId l'identifiant unique de l'adresse à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link AddressDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si l'adresse n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{addId}")
    @Operation(
            summary = "Récupère une adresse par son ID.",
            description = "Cette route permet de récupérer une adresse spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adresse récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Adresse non trouvée.")
    })
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addId) {
        Optional<AddressDTO> optionalAddress = addressService.findById(addId);

        if (optionalAddress.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalAddress.get(), HttpStatus.OK);
    }

    /**
     * Crée une nouvelle adresse en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * <p>Le service peut lever des exceptions si le code postal ou la ville référencés
     * dans le DTO n'existe pas en base de données.</p>
     *
     * @param dto les données de l'adresse à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} vide avec le statut HTTP 201 Created
     * @throws IPostalCodeService.PostalCodeNotFoundException si le code postal référencé n'existe pas
     * @throws ICityService.CityNotFoundException si la ville référencée n'existe pas
     */
    @PostMapping()
    @Operation(
            summary = "Créer une nouvelle adresse.",
            description = "Cette route permet de créer une nouvelle adresse dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Adresse créée avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<AddressDTO> createAddress(@RequestBody @Valid AddressRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {
        AddressDTO addressCreated = addressService.create(dto);

        return new ResponseEntity<>(addressCreated, HttpStatus.CREATED);
    }

    /**
     * Met à jour intégralement une adresse existante à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs de l'adresse sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si l'adresse ou le pays référencé sont introuvables.</p>
     *
     * @param addId  l'identifiant unique de l'adresse à modifier, extrait de l'URL
     * @param dto les nouvelles données de l'adresse, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant la {@link AddressDTO} mise à jour
     *         avec le statut HTTP 200 OK
     * @throws IAddressService.AddressNotFoundException       si l'adresse ciblée n'existe pas en base
     * @throws IPostalCodeService.PostalCodeNotFoundException si le code postal référencé n'existe pas
     * @throws ICityService.CityNotFoundException             si la ville référencée n'existe pas
     */
    @PutMapping("/{addId}")
    @Operation(
            summary = "Modifie une adresse en base de données.",
            description = "Cette route permet de modifier une adresse en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adresse modifiée avec succès."),
            @ApiResponse(responseCode = "404", description = "L'adresse n'existe pas.")
    })
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addId, @RequestBody @Valid AddressRequestDTO dto) throws IAddressService.AddressNotFoundException, IPostalCodeService.PostalCodeNotFoundException, ICityService.CityNotFoundException {
        AddressDTO addressUpdated = addressService.update(addId, dto);

        return new ResponseEntity<>(addressUpdated, HttpStatus.OK);
    }

    /**
     * Supprime une adresse existante à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param addId l'identifiant unique de l'adresse à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi</li>
     * @throws IAddressService.AddressNotFoundException si l'adresse ciblée n'existe pas en base
     */
    @DeleteMapping("/{addId}")
    @Operation(
            summary = "Supprime une adresse par son ID.",
            description = "Cette route permet de supprimer une adresse spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Article supprimé avec succès.")
    })
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addId) throws IAddressService.AddressNotFoundException {
        addressService.delete(addId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
