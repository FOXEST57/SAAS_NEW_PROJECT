package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.CartDTO;
import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CartRequestDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IAddressService;
import com.mns.cda.saas_facturation.Iservice.ICartService;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
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
        try {
            return ResponseEntity.ok(cartService.findById(id));
        } catch (ICartService.CartNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    @Operation(summary = "Créer un panier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Panier créé"),
            @ApiResponse(responseCode = "409", description = "Pannier déjà existant en BDD portant la même référence")
    })
    public ResponseEntity<CartDTO> create(@Valid @RequestBody CartRequestDTO dto) throws ICustomerService.CustomerNotFoundException {

        try {
            // @RequestBody : Spring désérialise automatiquement le JSON reçu en CartRequestDTO
            CartDTO response = cartService.create(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
        } catch (ICustomerService.CustomerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un panier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Panier supprimé"),
            @ApiResponse(responseCode = "404", description = "Panier introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ICartService.CartNotFoundException {
        try {
            cartService.delete(id);
            return ResponseEntity.noContent().build(); // 204 : succès sans contenu retourné
        } catch (ICartService.CartNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

    @PutMapping("/modify/{id}")
    @Operation(summary = "Modifier un panier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Panier modifié"),
            @ApiResponse(responseCode = "404", description = "Panier introuvable"),
            @ApiResponse(responseCode = "409", description = "Panier déjà existant en BDD portant la même référence")
    })
    public ResponseEntity<CartDTO> update(@PathVariable Long id,
                                              @Valid
                                              @RequestBody CartRequestDTO dto)
            throws ICartService.CartNotFoundException, ICustomerService.CustomerNotFoundException {

        try {
            CartDTO updated = cartService.modify(id, dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ICartService.CartNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ICustomerService.CustomerNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
