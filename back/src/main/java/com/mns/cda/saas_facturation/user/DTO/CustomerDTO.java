package com.mns.cda.saas_facturation.user.DTO;

import com.mns.cda.saas_facturation.enumeration.AccountTypeEnum;
import com.mns.cda.saas_facturation.location.DTO.AddressDTO;

public record CustomerDTO(
        Long ctmId,
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        AddressDTO address,
        AccountTypeEnum accountType
) {
}