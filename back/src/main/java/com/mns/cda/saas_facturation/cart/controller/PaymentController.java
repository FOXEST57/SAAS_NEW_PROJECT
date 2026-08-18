package com.mns.cda.saas_facturation.cart.controller;

import com.mns.cda.saas_facturation.cart.DTO.PaymentDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.PaymentRequestDTO;
import com.mns.cda.saas_facturation.cart.Iservice.IPaymentService;
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
@RequestMapping("/payment")
@Tag(name = "Payment", description = "Routes de gestion des paiements.")
@CrossOrigin
public class PaymentController {

    private final IPaymentService paymentService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des paiements.",
            description = "Cette route permet de récupérer la liste de tous les paiements dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des paiements récupérée avec succès.")
    })
    public List<PaymentDTO> getAll() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un paiement par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement introuvable")
    })
    public ResponseEntity<PaymentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findById(id));
    }

    @PostMapping("")
    @Operation(summary = "Créer un paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paiement créé"),
            @ApiResponse(responseCode = "400", description = "Données du paiement invalides"),
            @ApiResponse(responseCode = "404", description = "Facture associée introuvable")
    })
    public ResponseEntity<PaymentDTO> create(
            @Valid @RequestBody PaymentRequestDTO dto) {

        PaymentDTO response = paymentService.create(dto);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Paiement supprimé"),
            @ApiResponse(responseCode = "404", description = "Paiement introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
