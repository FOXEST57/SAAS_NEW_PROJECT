package com.mns.cda.saas_facturation.user.controller;

import com.mns.cda.saas_facturation.security.AppUserDetails;
import com.mns.cda.saas_facturation.user.DTO.InvitationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.InvitationRequestDTO;
import com.mns.cda.saas_facturation.user.model.Invitation;
import com.mns.cda.saas_facturation.user.service.InvitationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/invitation")
@RestController
@Tag(name = "Invitation", description = "Routes de gestion des invitations.")
@CrossOrigin
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping("/validate")
    public InvitationDTO validate(@RequestParam String token) {
        return invitationService.validate(token);
    }

    @PostMapping("")
    public ResponseEntity<InvitationDTO> create(@AuthenticationPrincipal AppUserDetails user, @RequestBody InvitationRequestDTO dto) {
            InvitationDTO invitation = invitationService.create(user, dto);

            return new ResponseEntity<>(invitation, HttpStatus.OK);
    }
}
