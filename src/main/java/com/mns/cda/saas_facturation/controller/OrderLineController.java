package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.OrderLineDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateOrderLineDTO;
import com.mns.cda.saas_facturation.Iservice.IOrderLineService;
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
@RequestMapping("/order-line")
@Tag(name = "Order Line", description = "Routes de gestion des lignes de commande")
@CrossOrigin
public class OrderLineController {

    private final IOrderLineService orderLineService;

    @GetMapping("/{articleId}/{cartId}")
    @Operation(
            summary = "Récupère une ligne de commande.",
            description = "Cette route permet de récupérer une ligne de commande précise, identifiée par son article et son panier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne de commande récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Ligne de commande non trouvée.")
    })
    public OrderLineDTO getById(@PathVariable Long articleId,
                                      @PathVariable Long cartId) {
        return orderLineService.findById(articleId, cartId);
    }

    @GetMapping("/list-articles/{cartId}")
    @Operation(
            summary = "Récupère la liste des lignes de commande d'un panier.",
            description = "Cette route permet de récupérer toutes les lignes de commande associées à un panier donné."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des lignes de commande récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Panier non trouvé.")
    })
    public List<OrderLineDTO> getByCartId(@PathVariable Long cartId) {
        return orderLineService.findByCartId(cartId);
    }

    @GetMapping("/list-cart/{articleId}")
    @Operation(
            summary = "Récupère la liste des paniers contenant un article.",
            description = "Cette route permet de récupérer tous les paniers dans lesquels un article donné apparaît."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des paniers récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Article non trouvé.")
    })
    public List<CartDTO> getByArticleId(@PathVariable Long articleId) {
        return orderLineService.findByArtId(articleId);
    }


    @PostMapping("")
    @Operation(
            summary = "Crée une nouvelle association article/ligne de commande.",
            description = "Cette route permet de créer une nouvelle ligne de commande en associant un article et un panier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Association créée avec succès."),
            @ApiResponse(responseCode = "404", description = "Article ou ligne de commande non trouvé."),
            @ApiResponse(responseCode = "400", description = "Données invalides (référence vide, stock manquant, identifiants manquants).")
    })
    public ResponseEntity<OrderLineDTO> create(@Valid @RequestBody OrderLineRequestDTO dto) {

            return new ResponseEntity<>(orderLineService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{articleId}/{cartId}")
    @Operation(
            summary = "Met à jour la quantité d'une ligne de commande.",
            description = "Cette route permet de modifier la quantité commandée d'un article dans un panier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ligne de commande mise à jour avec succès."),
            @ApiResponse(responseCode = "404", description = "Ligne de commande non trouvée."),
            @ApiResponse(responseCode = "400", description = "Données invalides (quantité manquante ou négative)."),
            @ApiResponse(responseCode = "409", description = "Stock insuffisant pour la quantité demandée.")
    })
    public ResponseEntity<OrderLineDTO> update(
            @PathVariable Long articleId,
            @PathVariable Long cartId,
            @Valid @RequestBody UpdateOrderLineDTO dto) {
            return new ResponseEntity<>(orderLineService.update(articleId, cartId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{articleId}/{cartId}")
    @Operation(
            summary = "Supprime une ligne de commande.",
            description = "Cette route permet de supprimer un article d'un panier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Association supprimée avec succès."),
            @ApiResponse(responseCode = "404", description = "Association article/ligne de commande non trouvée.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long articleId,
                                       @PathVariable Long cartId) {
            orderLineService.delete(articleId, cartId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
