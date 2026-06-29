package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.ArticleDTO;
import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.Iservice.IMakerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Maker")
@Tag(name = "Fabricant", description = "Routes de gestion des fabricants")
@CrossOrigin
public class MakerController {

    protected final IMakerService makerService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des fabricants.",
            description = "Cette route permet de récupérer la liste de tous les fabricants dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des fabricants récupérée avec succès.")
    })
    public List<MakerDTO> getMakers() { return makerService.findAll(); }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupère un fabricant par son ID.",
            description = "Cette route permet de récupérer un fabricant spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fabricant récupéré avec succès."),
            @ApiResponse(responseCode = "404", description = "Fabricant non trouvé.")
    })
    public ResponseEntity<MakerDTO> getMakerById(@PathVariable Long id) throws IMakerService.MakerNotFoundException {

        return ResponseEntity.ok(makerService.findById(id));
    }

    public ResponseEntity<MakerDTO> create (@Valid @RequestBody MakerRequestDTO dto) {
        return new ResponseEntity<>(makerService.create(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprime un fabricant par son ID.",
            description = "Cette route permet de supprimer un fabricant spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fabricant supprimé avec succès."),
            @ApiResponse(responseCode = "404", description = "Le fabricant n'existe pas.")
    })

    public ResponseEntity<Void> delete(@PathVariable Long id) throws IMakerService.MakerNotFoundException {
        makerService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<MakerDTO> update (
            @PathVariable Long id,
            @Valid @RequestBody MakerRequestDTO dto) throws IMakerService.MakerNotFoundException {

        return new ResponseEntity<>(makerService.modify(id, dto), HttpStatus.OK);
    }
}
