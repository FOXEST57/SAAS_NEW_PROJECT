package com.mns.cda.saas_facturation.user.DTO;

import com.mns.cda.saas_facturation.location.DTO.AddressDTO;
import com.mns.cda.saas_facturation.user.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.user.DTO.responseDTO.CustomerResponseDTO;

import java.util.List;

public record CustomerDTO(
        Long ctmId,
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        AddressDTO address,
        AccountTypeResponseDTO accountType,
        List<CustomerResponseDTO> customers
) {
}