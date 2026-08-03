package com.mns.cda.saas_facturation.user.DTO.responseDTO;

import com.mns.cda.saas_facturation.location.DTO.AddressDTO;

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