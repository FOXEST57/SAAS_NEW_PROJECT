package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.IMakerReferenceService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/maker-reference")
@Tag(name = "Maker Reference", description = "Routes de gestion des références fabricants")
@CrossOrigin
public class MakerReferenceController {

    private final IMakerReferenceService makerReferenceService;

    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des références fabricant.",
            description = "Cette route permet de récupérer la liste de toutes les références fabricant dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des références fabricants récupérée avec succès.")
    })
    public List<MakerReferenceDTO> getAll() {
        return makerReferenceService.findAll();
    }

    @GetMapping("/{artId}/{mkrId}")
    @Operation(
            summary = "Récupère la liste des références fabricants par association article, fabricant.",
            description = "Cette route permet de récupérer la liste de toutes les références fabricants par association article, fabricant dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des références fabricant par article récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Association article,fabricant non trouvé.")
    })
    public ResponseEntity<MakerReferenceDTO> getById(@PathVariable Long artId,
                                                             @PathVariable Long mkrId) {
        try {
            return new ResponseEntity<>(makerReferenceService.findById(artId, mkrId), HttpStatus.OK);
        } catch (IMakerReferenceService.MakerReferenceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/list-maker/{artId}")
    public List<MakerDTO> findByArticleId(@PathVariable Long artId) {
        return makerReferenceService.findAllByArticle(artId);
    }

    @GetMapping("list-article/{mkrId}")
    public List<ArticleResponseMakerReferenceDTO> findByMkrId(@PathVariable Long mkrId) {
        return makerReferenceService.findAllByMaker(mkrId);
    }

    @PostMapping("")
    @Operation(
            summary = "Crée une nouvelle association article/fabricant.",
            description = "Cette route permet de créer une nouvelle référence fabricant en associant un article et un fabricant existants, avec une référence et un stock initial."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Association créée avec succès."),
            @ApiResponse(responseCode = "404", description = "Article ou fabricant non trouvé."),
            @ApiResponse(responseCode = "400", description = "Données invalides (référence vide, stock manquant, identifiants manquants).")
    })
    public ResponseEntity<MakerReferenceDTO> create(@Valid @RequestBody MakerReferenceRequestDTO dto) {
        try {
            return new ResponseEntity<>(makerReferenceService.create(dto), HttpStatus.CREATED);
        } catch (IArticleService.ArticleNotFoundException | IMakerService.MakerNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{artId}/{mkrId}")
    @Operation(
            summary = "Met à jour la référence et le stock d'une association article/fabricant.",
            description = "Cette route permet de modifier la référence fabricant et le stock pour une association article/fabricant existante, identifiée par son artId et son mkrId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Association mise à jour avec succès."),
            @ApiResponse(responseCode = "404", description = "Association article/fabricant non trouvée."),
            @ApiResponse(responseCode = "400", description = "Données invalides (référence vide ou stock manquant).")
    })
    public ResponseEntity<MakerReferenceDTO> update(
            @PathVariable Long artId,
            @PathVariable Long mkrId,
            @Valid @RequestBody UpdateMakerReferenceDTO dto) {
        try {
            return new ResponseEntity<>(makerReferenceService.modify(artId, mkrId, dto), HttpStatus.OK);
        } catch (IMakerReferenceService.MakerReferenceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{artId}/{mkrId}")
    @Operation(
            summary = "Supprime une association article/fabricant.",
            description = "Cette route permet de supprimer une association entre un article et un fabricant, identifiée par son artId et son mkrId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Association supprimée avec succès."),
            @ApiResponse(responseCode = "404", description = "Association article/fabricant non trouvée.")
    })
    public ResponseEntity<Void> delete(@PathVariable Long artId,
                                       @PathVariable Long mkrId) {
        try {
            makerReferenceService.delete(artId, mkrId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IMakerReferenceService.MakerReferenceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
