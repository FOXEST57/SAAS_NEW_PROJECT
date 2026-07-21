package com.mns.cda.saas_facturation.DTO.responseDTO;

import com.mns.cda.saas_facturation.DTO.AddressDTO;

import java.util.List;

public record CustomerResponseDTO(
        Long ctmId,
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        AddressDTO address,
        AccountTypeResponseDTO accountType
) {
}