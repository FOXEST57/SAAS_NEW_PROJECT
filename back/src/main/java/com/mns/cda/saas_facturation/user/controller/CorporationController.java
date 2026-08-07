package com.mns.cda.saas_facturation.user.controller;


import com.mns.cda.saas_facturation.security.AppUserDetails;
import com.mns.cda.saas_facturation.user.DTO.CorporationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CorporationRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.ICorporationService;
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
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/corporation")
@RestController
@Tag(name = "Corporation", description = "Routes de gestion des entreprises.")
@CrossOrigin
public class CorporationController {

    private final ICorporationService corporationService;
    
    @GetMapping("/list")
    @Operation(
            summary = "Récupère la liste des entreprises.",
            description = "Cette route permet de récupérer la liste de toutes les entreprises dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des entreprises récupérée avec succès.")
    })
    public List<CorporationDTO> getCorporations() {
        return corporationService.findAll();
    }

    @GetMapping("/{corpoId}")
    @Operation(
            summary = "Récupère une entreprise par son ID.",
            description = "Cette route permet de récupérer une entreprise spécifique par son ID dans la base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entreprise récupérée avec succès."),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée.")
    })
    public ResponseEntity<CorporationDTO> getCorporationById(@PathVariable Long corpoId) {
        Optional<CorporationDTO> optionalCorporation = corporationService.findById(corpoId);

        if (optionalCorporation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCorporation.get(), HttpStatus.OK);
    }

    @PutMapping()
    @Operation(
            summary = "Modifie une entreprise en base de données.",
            description = "Cette route permet de modifier une entreprise en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entreprise modifiée avec succès.")
    })
    public ResponseEntity<CorporationDTO> updateCorporation(@AuthenticationPrincipal AppUserDetails appUserDetails, @RequestBody @Valid CorporationRequestDTO dto) {
            CorporationDTO corporationUpdated = corporationService.update(appUserDetails.getUser().getCtmId(), dto);

            return new ResponseEntity<>(corporationUpdated, HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(
            summary = "Supprime un entreprise par son ID.",
            description = "Cette route permet de supprimer un entreprise spécifique par son ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entreprise supprimé avec succès."),
    })
    public ResponseEntity<Void> deleteCorporation( @AuthenticationPrincipal AppUserDetails userDetails) {
        corporationService.delete(userDetails.getUser().getCtmId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
