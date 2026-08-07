package com.mns.cda.saas_facturation.cart.controller;


import com.mns.cda.saas_facturation.cart.DTO.CartDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchCartStatus;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.ICartService;
import com.mns.cda.saas_facturation.security.AppUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Routes de gestion des paniers.")
@CrossOrigin
public class CartController {

    private final ICartService cartService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des Paniers.",
            description = "Cette route permet de récupérer la liste de tous les paniers dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des paniers récupérée avec succès.")
    })
    public List<CartDTO> getAll() {
        return cartService.findAll();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un panier par crtlId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Panier trouvé"),
            @ApiResponse(responseCode = "404", description = "Panier introuvable")
    })
    public ResponseEntity<CartDTO> findById(@PathVariable Long id) {
            return ResponseEntity.ok(cartService.findById(id));
    }

    @PostMapping("")
    @Operation(summary = "Créer un panier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Panier créé"),
            @ApiResponse(responseCode = "409", description = "Pannier déjà existant en BDD portant la même référence")
    })
    public ResponseEntity<CartDTO> create(@AuthenticationPrincipal AppUserDetails userDetails, @Valid @RequestBody CartRequestDTO dto) {

            CartDTO response = cartService.create(userDetails, dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    @PostMapping("/{id}/revisit")
    @Operation(summary = "Crée un panier liée a un Devis pour révision.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Panier créé à partir du devis"),
            @ApiResponse(responseCode = "404", description = "Devis introuvable")
    })
    public CartDTO revisitQuote(@PathVariable Long id) {
        return cartService.quoteToRevisitedCart( id);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un panier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Panier supprimé"),
            @ApiResponse(responseCode = "404", description = "Panier introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
            cartService.delete(id);
            return ResponseEntity.noContent().build(); // 204 : succès sans contenu retourné
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un panier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Panier modifié"),
            @ApiResponse(responseCode = "404", description = "Panier introuvable"),
            @ApiResponse(responseCode = "409", description = "Panier déjà existant en BDD portant la même référence")
    })
    public ResponseEntity<CartDTO> update(@PathVariable Long id,
                                              @Valid
                                              @RequestBody CartRequestDTO dto) {
            CartDTO updated = cartService.modify(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/status")
    @Operation(summary = "Modifie le status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status du panier modifié"),
            @ApiResponse(responseCode = "404", description = "Panier introuvable")
    })
    public ResponseEntity<CartDTO> patchStatus(@Valid @RequestBody PatchCartStatus dto) {
        CartDTO updated = cartService.patchStatus(dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
