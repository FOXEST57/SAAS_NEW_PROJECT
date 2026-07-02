package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerMapper {

    private final AddressMapper addressMapper;

    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCtmFirstName(),
                customer.getCtmLastName(),
                customer.getCtmEmail(),
                customer.getCtmPhone(),
                addressMapper.toDTO(customer.getAddress())
        );
    }

}
