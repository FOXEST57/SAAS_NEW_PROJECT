package com.mns.cda.saas_facturation.DTO;

import com.mns.cda.saas_facturation.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.CustomerResponseDTO;

import java.util.List;

public record CustomerDTO(
        Long ctmId,
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        AddressDTO address,
        AccountTypeResponseDTO accountType,
        String password,
        List<CustomerResponseDTO> customers
) {
}