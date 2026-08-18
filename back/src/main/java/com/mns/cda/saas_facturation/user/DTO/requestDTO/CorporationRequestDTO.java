package com.mns.cda.saas_facturation.user.DTO.requestDTO;


import com.mns.cda.saas_facturation.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CorporationRequestDTO(
    @NotBlank String corpName,
    @NotBlank String corpSiret,
    @NotBlank String corpTva,
    @NotBlank @Email String corpEmail,
    @NotBlank @ValidPhoneNumber String corpPhone,
    String corpPreRefQuote,
    String corpPreRefInvoice,
    String corpPreRefCart,
    @NotBlank String corpIban,
    String corpTag,
    @NotNull Long addId
){
}
