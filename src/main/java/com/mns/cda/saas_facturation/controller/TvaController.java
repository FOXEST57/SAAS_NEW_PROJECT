package com.mns.cda.saas_facturation.controller;


import com.mns.cda.saas_facturation.DTO.TvaRequestDTO;
import com.mns.cda.saas_facturation.DTO.UpdateTvaTauxDTO;
import com.mns.cda.saas_facturation.Iservice.ITvaService;
import com.mns.cda.saas_facturation.model.Tva;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/tva")
@Tag(name = "Tva", description = "Routes de gestion des articles.")
@CrossOrigin
public class TvaController {

    protected final ITvaService tvaService;

    @GetMapping("/list")
    @Operation(summary = "Récupère la liste des articles",
            description = "Cette route permet de récupérer la liste de toutes les tva en base de données.")
    public List<Tva> getTva()  {
        return tvaService.findAll();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Récupère une tva par son ID.",
            description = "Cette route permet de récupérer une tva spécifique par son ID dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tva récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Tva non trouvé.")
    })
    public ResponseEntity<Tva> getTvaById(
            @PathVariable Long id) {
        Optional<Tva> optionalTva = tvaService.findById(id);

        if (optionalTva.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(optionalTva.get(), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle tva.",
            description = "Cette route permet de créer une nouvelle tva dans la base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tva créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Requête invalide.")
    })
    public ResponseEntity<Tva> create(
            @Valid @RequestBody TvaRequestDTO tva
    ) {
        Tva response = tvaService.create(tva);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une tva par son ID.",
            description = "Cette route permet de supprimer une tva spécifique par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tva supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "La Tva n'existe pas.")
    })
    public ResponseEntity<Tva> delete(
            @PathVariable Long id) {

        Optional<Tva> optionalTva = tvaService.findById(id);

        if (optionalTva.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        tvaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifie une Tva en base de données.",
            description = "Cette route permet de modifier une Tva en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tva modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "La Tva n'existe pas.")
    })
    public ResponseEntity<Tva> update(
            @PathVariable Long id,
            @RequestBody TvaRequestDTO tva) throws ITvaService.TvaNotFoundException {
        Tva updatedTva = tvaService.update(id, tva);
        return new ResponseEntity<>(updatedTva, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifie le taux d'une Tva en base de données.",
            description = "Cette route permet de modifier le taux de Tva en base de données.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tva modifié avec succès."),
            @ApiResponse(responseCode = "404", description = "La Tva n'existe pas.")
    })
    public ResponseEntity<Tva> patchTaux(
            @PathVariable Long id,
            UpdateTvaTauxDTO tvaTauxDTO) throws ITvaService.TvaNotFoundException {
        Optional<Tva> optionalTva = tvaService.findById(id);
        if (optionalTva.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Tva response = tvaService.patchTaux(id, tvaTauxDTO.tvaTaux());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
