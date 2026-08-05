package com.mns.cda.saas_facturation.cart.controller;

import com.mns.cda.saas_facturation.cart.DTO.InvoiceDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.InvoiceRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.patchDTO.PatchInvoiceStatus;
import com.mns.cda.saas_facturation.cart.Iservice.IInvoiceService;
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
@RequestMapping("/invoice")
@Tag(name = "Invoice", description = "Routes de gestion des factures.")
@CrossOrigin
public class InvoiceController {

    private final IInvoiceService invoiceService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des Factures.",
            description = "Cette route permet de récupérer la liste de toutes les factures dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des factures récupérée avec succès.")
    })
    public List<InvoiceDTO> getAll() {
        return invoiceService.findAll();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une facture par cmdId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facture trouvée"),
            @ApiResponse(responseCode = "404", description = "Facture introuvable")
    })
    public ResponseEntity<InvoiceDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @PostMapping("")
    @Operation(summary = "Créer une facture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facture créée"),
            @ApiResponse(responseCode = "409", description = "Facture déjà existante en BDD portant la même référence")
    })
    public ResponseEntity<InvoiceDTO> create(@Valid @RequestBody InvoiceRequestDTO dto) {

        InvoiceDTO response = invoiceService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une facture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Facture supprimée"),
            @ApiResponse(responseCode = "404", description = "Facture introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 : succès sans contenu retourné
    }

    @PatchMapping("")
    @Operation(summary = "Modifie le status d'une facture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facture status modifiée"),
            @ApiResponse(responseCode = "404", description = "Facture introuvable"),
            @ApiResponse(responseCode = "409", description = "Facture déjà existante en BDD portant la même référence")
    })
    public ResponseEntity<InvoiceDTO> updateStatusInvoice(@Valid @RequestBody PatchInvoiceStatus dto) {
        return new ResponseEntity<>(invoiceService.updateStatus(dto), HttpStatus.OK);
    }
}
