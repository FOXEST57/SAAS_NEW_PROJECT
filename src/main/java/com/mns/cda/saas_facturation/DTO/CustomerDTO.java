package com.mns.cda.saas_facturation.DTO;

public record CustomerDTO(
        Long ctmId,
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        AddressDTO address
) {
}