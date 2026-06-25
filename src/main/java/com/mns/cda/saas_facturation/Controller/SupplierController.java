package com.mns.cda.saas_facturation.Controller;

import com.mns.cda.saas_facturation.model.Supplier;
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
 * Contrôleur REST gérant les opérations CRUD sur les fournisseurs.
 * Toutes les routes sont préfixées par /supplier (défini dans @RequestMapping).
 * La documentation Swagger est accessible sur /swagger-ui/index.html.
 */
@RestController                 // Indique à Spring que cette classe gère des requêtes HTTP et retourne du JSON
@RequiredArgsConstructor        // Lombok : génère automatiquement un constructeur avec les champs final
@CrossOrigin                    // Autorise les appels depuis d'autres domaines (ex: frontend Angular)
@RequestMapping("/supplier") // Préfixe commun à toutes les routes de ce contrôleur
@Tag(name = "Supplier", description = "Gestion des fournisseurs")
public class SupplierController {

    // Injection du service via l'interface — Spring injecte automatiquement SupplierService
    protected final ISupplierService supplierService;

    /**
     * Retourne la liste complète des fournisseurs.
     * GET /supplier/list
     */
    @GetMapping("/list")
    @Operation(summary = "Lister tous les fournisseurs", description = "Permet d'accèder à la liste des fournisseurs")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    public List<Supplier> findAll() {
        return supplierService.findAll();
    }

    /**
     * Retourne un fournisseur identifié par son id.
     * GET /supplier/{id}
     * Lève SupplierNotFoundException (404) si l'id n'existe pas en base.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un fournisseur par id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable")
    })
    public ResponseEntity<Supplier> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(supplierService.findById(id).get());
        } catch (ISupplierService.SupplierNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Crée un nouveau fournisseur à partir des données reçues dans le corps de la requête.
     * POST /supplier
     * Retourne le fournisseur créé avec le statut 201 Created.
     */
    @PostMapping("")
    @Operation(summary = "Créer un fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fournisseur créé"),
            @ApiResponse(responseCode = "409", description = "Fournisseur déjà existant")
    })
    public ResponseEntity<Supplier> create(@Valid @RequestBody Supplier supplierToInsert) {
        // @RequestBody : Spring désérialise automatiquement le JSON reçu en objet Supplier

        try {
            supplierService.create(supplierToInsert);
            return new ResponseEntity<>(supplierToInsert, HttpStatus.CREATED); // 201
        } catch (ISupplierService.ExistingSupplierException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409
        }
    }

    /**
     * Supprime le fournisseur correspondant à l'id.
     * DELETE /supplier/{id}
     * Retourne 204 No Content si supprimé, 404 si introuvable.
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
     * PUT /supplier/{id}
     * Retourne 204 No Content si modifié, 404 si l'id est introuvable.
     */
    @PutMapping("/modify/{id}")
    @Operation(summary = "Modifier un fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fournisseur modifié"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "409", description = "Fournisseur déjà existant")
    })
    public ResponseEntity<Void> update(@PathVariable long id,
                                       @Valid
                                       @RequestBody Supplier supplierToUpdate)
            throws ISupplierService.SupplierNotFoundException, ISupplierService.ExistingSupplierException {

        try {
            supplierService.modify(id, supplierToUpdate);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        } catch (ISupplierService.SupplierNotFoundException e) {
            // Si le fournisseur n'existe pas, on retourne une réponse 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        } catch (ISupplierService.ExistingSupplierException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409
        }
    }
}