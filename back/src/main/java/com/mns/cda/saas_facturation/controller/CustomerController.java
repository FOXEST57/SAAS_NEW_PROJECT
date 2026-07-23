package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.*;
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
 * Contrôleur REST exposant les endpoints de gestion des clients.
 *
 * <p>Ce contrôleur prend en charge les opérations CRUD sur la ressource {@code Customer}.
 * Toutes les routes sont préfixées par {@code /customer} et retournent des données
 * au format JSON.</p>
 *
 * <p>La logique métier est entièrement déléguée à {@link ICustomerService}.
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
 * <p>L'exception métier ({@link ResourceNotFoundException} est propagée vers la couche de gestion globale des erreurs.</p>
 *
 * @see ICustomerService
 * @see CustomerDTO
 * @see CustomerRequestDTO
 */
@RequiredArgsConstructor
@RequestMapping("/customer")
@RestController
@Tag(name = "Customer", description = "Routes de gestion des clients.")
@CrossOrigin
public class CustomerController {

    /**
     * Service métier de gestion des clients, injecté par constructeur via {@code @RequiredArgsConstructor}.
     * L'utilisation de l'interface {@link ICustomerService} garantit le découplage
     * entre le contrôleur et l'implémentation concrète du service.
     */
    private final ICustomerService customerService;

    /**
     * Récupère la liste complète de tous les clients enregistrés en base de données.
     *
     * <p>Les clients sont retournés sous forme de {@link CustomerDTO} afin de ne pas
     * exposer directement les entités JPA au client.</p>
     *
     * @return la {@link List} de {@link CustomerDTO} (vide si aucun client n'existe),
     *         avec le statut HTTP 200 OK
     */
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des clients.",
            description = "Cette route permet de récupérer la liste de tous les clients dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès.")
    })
    public List<CustomerDTO> getCustomers() {
        return customerService.findAll();
    }

    /**
     * Récupère un client spécifique à partir de son identifiant unique.
     *
     * <p>Si aucun client ne correspond à l'ID fourni, une réponse 404 est retournée
     * sans corps, conformément aux conventions REST.</p>
     *
     * @param ctmId l'identifiant unique du client à récupérer, extrait de l'URL
     * @return une {@link ResponseEntity} contenant :
     *         <ul>
     *           <li>l'{@link CustomerDTO} correspondant avec le statut 200 OK si trouvé</li>
     *           <li>un corps vide avec le statut 404 Not Found si le client n'existe pas</li>
     *         </ul>
     */
    @GetMapping("/{ctmId}")
    @Operation(
            summary = "Récupère un client par son ID.",
            description = "Cette route permet de récupérer un client spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Client non trouvé.")
    })
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long ctmId) {
        Optional<CustomerDTO> optionalCustomer = customerService.findById(ctmId);

        if (optionalCustomer.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCustomer.get(), HttpStatus.OK);
    }

    /**
     * Crée un nouveau client en base de données à partir des données fournies dans le corps de la requête.
     *
     * <p>Le DTO est validé automatiquement par Bean Validation ({@code @Valid}) avant
     * d'atteindre la logique métier. En cas d'échec de validation, le
     * {@code GlobalExceptionInterceptor} intercepte l'exception et retourne un 400
     * avec le détail des champs invalides.</p>
     *
     * <p>Le service peut lever des exceptions si l'adresse référencée dans le DTO
     * n'existe pas en base de données.</p>
     *
     * @param dto les données du client à créer, désérialisées depuis le corps JSON
     *            de la requête et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link CustomerDTO} du client créé
     *         (avec son ID généré) et le statut HTTP 201 Created
     * @throws ResourceNotFoundException si l'adresse référencée n'existe pas
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouveau client.",
            description = "Cette route permet de créer un nouveau client dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody @Valid CustomerRequestDTO dto) {
        CustomerDTO customerCreated = customerService.create(dto);

        return new ResponseEntity<>(customerCreated, HttpStatus.CREATED);
    }

    /**
     * Met à jour intégralement un client existant à partir de son identifiant.
     *
     * <p>Cette opération correspond à un remplacement complet (sémantique HTTP PUT) :
     * tous les champs du client sont écrasés par les valeurs fournies dans le DTO.</p>
     *
     * <p>Le DTO est validé par Bean Validation avant traitement. Les exceptions métier
     * sont propagées si le client ou l'adresse référencée sont introuvables.</p>
     *
     * @param ctmId  l'identifiant unique du client à modifier, extrait de l'URL
     * @param dto les nouvelles données du client, désérialisées depuis le corps JSON
     *            et validées par {@code @Valid}
     * @return une {@link ResponseEntity} contenant l'{@link CustomerDTO} mis à jour
     *         avec le statut HTTP 200 OK
     * @throws ResourceNotFoundException si le client ciblé ou l'adresse référencée n'existe pas
     */
    @PutMapping("/{ctmId}")
    @Operation(
            summary = "Modifie un client en base de données.",
            description = "Cette route permet de modifier un client en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client modifié avec succès.")
    })
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long ctmId, @RequestBody @Valid CustomerRequestDTO dto) {
            CustomerDTO customerUpdated = customerService.update(ctmId, dto);

            return new ResponseEntity<>(customerUpdated, HttpStatus.OK);
    }

    /**
     * Supprime un client existant à partir de son identifiant unique.
     *
     * <p>En cas de succès, une réponse 204 No Content est retournée conformément
     * aux conventions REST (pas de corps dans la réponse après suppression).</p>
     *
     * @param ctmId l'identifiant unique du client à supprimer, extrait de l'URL
     * @return une {@link ResponseEntity} vide avec le statut 204 No Content si la suppression a réussi
     * @throws ResourceNotFoundException si le client n'existe pas en base
     */
    @DeleteMapping("/{ctmId}")
    @Operation(
            summary = "Supprime un client par son ID.",
            description = "Cette route permet de supprimer un client spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client supprimé avec succès."),
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long ctmId) {
        customerService.delete(ctmId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
