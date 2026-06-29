package com.mns.cda.saas_facturation.DTO;

public record CustomerDTO(
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        String ctmAddress,
        CityDTO city
) {
}