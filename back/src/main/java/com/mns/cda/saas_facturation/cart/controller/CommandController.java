package com.mns.cda.saas_facturation.cart.controller;


import com.mns.cda.saas_facturation.cart.DTO.CommandDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CommandRequestDTO;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.PatchCommandStatus;
import com.mns.cda.saas_facturation.cart.Iservice.ICommandService;
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
@RequestMapping("/command")
@Tag(name = "Command", description = "Routes de gestion des commandes.")
@CrossOrigin
public class CommandController {

    private final ICommandService commandService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des Commandes.",
            description = "Cette route permet de récupérer la liste de toutes les commandes dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès.")
    })
    public List<CommandDTO> getAll() {
        return commandService.findAll();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande par cmdId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande trouvée"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable")
    })
    public ResponseEntity<CommandDTO> findById(@PathVariable Long id) {
            return ResponseEntity.ok(commandService.findById(id));
    }

    @PostMapping("")
    @Operation(summary = "Créer une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande créée"),
            @ApiResponse(responseCode = "409", description = "Commande déjà existante en BDD portant la même référence")
    })
    public ResponseEntity<CommandDTO> create(@Valid @RequestBody CommandRequestDTO dto) {

            CommandDTO response = commandService.create(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Commande supprimée"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
            commandService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 : succès sans contenu retourné
    }

    @PatchMapping("")
    @Operation(summary = "Modifie le status d'une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande status modifiée"),
            @ApiResponse(responseCode = "404", description = "Commande introuvable"),
            @ApiResponse(responseCode = "409", description = "Commande déjà existante en BDD portant la même référence")
    })
    public ResponseEntity<CommandDTO> update(@Valid
                                             @RequestBody PatchCommandStatus dto) {
            CommandDTO updated = commandService.patchStatus(dto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
