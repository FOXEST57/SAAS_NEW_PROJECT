package com.mns.cda.saas_facturation.user.controller;


import com.mns.cda.saas_facturation.user.DTO.AccountTypeDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.AccountTypeRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.IAccountTypeService;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/AccountType")
@Tag(name = "AccountType", description = "Routes de gestion des différents types de compte")
@CrossOrigin
public class AccountTypeController {

    protected final IAccountTypeService AccountTypeService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des types de compte.",
            description = "Cette route permet de récupérer la liste de tous les types de compte dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des types de compte récupérée avec succès.")
    })
    public List<AccountTypeDTO> getAccountTypes() { return AccountTypeService.findAll(); }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupère un type de compte par son ID.",
            description = "Cette route permet de récupérer un type de compte spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Type de compte récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Type de compte non trouvé.")
    })
    public ResponseEntity<AccountTypeDTO> getAccountTypeById(@PathVariable Long id) throws ResourceNotFoundException {

        try {
            return new ResponseEntity<>(AccountTypeService.findById(id),HttpStatus.OK);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    @Operation(
            summary = "Crée un nouveau type de compte.",
            description = "Cette route permet de créer un nouveau type de compte dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Type de compte créé avec succès."),
            @ApiResponse(responseCode = "400", description = "Données invalides.")
    })
    public ResponseEntity<AccountTypeDTO> create (@Valid @RequestBody AccountTypeRequestDTO dto) {
        return new ResponseEntity<>(AccountTypeService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprime un type de compte par son ID.",
            description = "Cette route permet de supprimer un type de compte spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Type de compte supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "Le type de compte n'existe pas.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ResourceNotFoundException {
        try {
            AccountTypeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Met à jour un type de compte existant.",
            description = "Cette route permet de modifier les informations d'un type de compte existant, identifié par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Type de compte mis à jour avec succès."),
            @ApiResponse(responseCode = "404", description = "Type de compte non trouvé."),
            @ApiResponse(responseCode = "400", description = "Données invalides.")
    })
    public ResponseEntity<AccountTypeDTO> update (
            @PathVariable Long id,
            @Valid @RequestBody AccountTypeRequestDTO dto) throws ResourceNotFoundException {

        try {
            return new ResponseEntity<>(AccountTypeService.modify(id, dto),HttpStatus.OK);
        } catch (ResourceNotFoundException _) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
