package com.mns.cda.saas_facturation.product.controller;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.DTO.MakerDTO;
import com.mns.cda.saas_facturation.product.DTO.DeliveryDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.DeliveryRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.UpdateDeliveryDTO;
import com.mns.cda.saas_facturation.product.Iservice.IDeliveryService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
@Tag(name = "Delivery", description = "Routes de gestion des commandes auprès des fabricants et fournisseurs")
@CrossOrigin
public class DeliveryController {

    private final IDeliveryService deliveryService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des commandes auprès des fabricants et fournisseurs.",
            description = "Cette route permet de récupérer la liste de toutes les commandes effectuées auprès des fabricants et fournisseurs dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commandes des fabricants et fournisseurs récupérée avec succès.")
    })
    public List<DeliveryDTO> getAll() {
        return deliveryService.findAll();
    }

    @GetMapping("/{dlvId}")
    @Operation(
            summary = "Récupère une commande d'un fabricant ou fournisseur par son id.",
            description = "Cette route permet de récupérer la commande d'un fabricant ou fournisseur par son id dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande du fabricant ou fournisseur récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée.")
    })
    public ResponseEntity<DeliveryDTO> getById(@PathVariable Long dlvId) {
        try {
            return new ResponseEntity<>(deliveryService.findById(dlvId), HttpStatus.OK);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(
            summary = "Crée une nouvelle commande d'un fabricant ou fournisseur.",
            description = "Cette route permet de créer une nouvelle commande d'un fabricant ou fournisseur."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande créée avec succès."),
            @ApiResponse(responseCode = "404", description = "Association article/fabricant ou article/fournisseur non trouvée."),
            @ApiResponse(responseCode = "400", description = "Données invalides (quantité manquante, prix d'achat manquant).")
    })
    public ResponseEntity<DeliveryDTO> create(@Valid @RequestBody DeliveryRequestDTO dto) {
        try {
            return new ResponseEntity<>(deliveryService.create(dto), HttpStatus.CREATED);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{dlvId}")
    @Operation(
            summary = "Met à jour la quantité et le prix d'achat d'une commande d'un fabricant ou fournisseur.",
            description = "Cette route permet de modifier la quantité et le prix d'achat d'une commande d'un fabricant ou fournisseur existante, identifiée par son dlvId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès."),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée."),
            @ApiResponse(responseCode = "400", description = "Données invalides (quantité manquante ou prix manquant).")
    })
    public ResponseEntity<DeliveryDTO> update(@PathVariable Long dlvId, @Valid @RequestBody UpdateDeliveryDTO dto) {
        try {
            return new ResponseEntity<>(deliveryService.modify(dlvId, dto), HttpStatus.OK);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{dlvId}")
    @Operation(
            summary = "Met à jour le statut d'une commande d'un fabricant ou fournisseur.",
            description = "Cette route permet de modifier le statut d'une commande d'un fabricant ou fournisseur existante, identifiée par son dlvId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès."),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée."),
            @ApiResponse(responseCode = "400", description = "Données invalides.")
    })
    public ResponseEntity<DeliveryDTO> updateStatus(@PathVariable Long dlvId, @RequestBody DeliveryStatus status) {
        try {
            return new ResponseEntity<>(deliveryService.modifyStatus(dlvId, status), HttpStatus.OK);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{dlvId}")
    @Operation(
            summary = "Supprime une commande d'un fabricant ou fournisseur.",
            description = "Cette route permet de supprimer une commande d'un fabricant ou fournisseur, identifiée par son dlvId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Commande supprimée avec succès."),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long dlvId) {
        try {
            deliveryService.delete(dlvId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
