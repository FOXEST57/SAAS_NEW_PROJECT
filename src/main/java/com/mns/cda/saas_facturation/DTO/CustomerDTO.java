package com.mns.cda.saas_facturation.DTO;

import java.util.List;

public record CustomerDTO(
        Long ctmId,
        String ctmFirstName,
        String ctmLastName,
        String ctmEmail,
        String ctmPhone,
        AddressDTO address,
        List<CustomerDTO> customers,
        List<CustomerDTO> superCustomers
) {
}