package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.CustomerRequestDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.LogInDTO;
import com.mns.cda.saas_facturation.Iservice.ICustomerService;
import com.mns.cda.saas_facturation.security.AppUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final ICustomerService customerService;
    private final AuthenticationProvider authenticationProvider;
    protected String jwtSecret = "Saas";

    @PostMapping("/signUp")
    @Operation(summary = "Permet d'enregistrer un utilisateur en base de données",
            description = "Cette route permet à l'utilisateur de créer un compte utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur enregistré avec succès")
    })
    public ResponseEntity<CustomerDTO> signUp(
            @RequestBody @Validated CustomerRequestDTO customer) {
        return new ResponseEntity<>(customerService.create(customer), HttpStatus.OK);
    }

    @PostMapping("/logIn")
    @Operation(summary = "Permet aux utilisateurs de se connecter",
            description = "Cette route permet à l'utilisateur de se connecter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur connecté avec succès"),
            @ApiResponse(responseCode = "403", description = "Connexion non autorisée.")
    })
    public ResponseEntity<String> logIn(
            @RequestBody LogInDTO user) {
        try {
            AppUserDetails appUser = (AppUserDetails) authenticationProvider
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            user.ctmEmail(),
                            user.password())
                    ).getPrincipal();

            String jwt = Jwts.builder()
                    .setSubject(user.ctmEmail())
                    .addClaims(Map.of("role",appUser.getUser().getAccountType().getAccTypeLibelle()))
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


}
