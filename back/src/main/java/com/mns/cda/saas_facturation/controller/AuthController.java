package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.model.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final ICustomerService customerService;
    private final AuthenticationProvider authenticationProvider;
    protected String jwtSecret = "Saas";

    @PostMapping("/sign-up")
    @Operation(summary = "Permet de d'enregistrer un utilisateur en base de données",
            description = "Cette route permet à l'utilisateur de crée un compte utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur enregistré avec succès")
    })
    public ResponseEntity<CustomerDTO> signUp(
            @RequestBody @Validated CustomerRequestDTO customer) {
        return new ResponseEntity<>(customerService.create(customer), HttpStatus.OK);
    }



}
