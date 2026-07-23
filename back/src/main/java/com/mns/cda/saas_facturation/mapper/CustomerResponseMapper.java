package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.CustomerResponseDTO;
import com.mns.cda.saas_facturation.mapper.responseMapper.AccountTypeResponseMapper;
import com.mns.cda.saas_facturation.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerResponseMapper {

    private final AddressMapper addressMapper;
    private final AccountTypeResponseMapper accountTypeResponseMapper;

    public CustomerResponseDTO toDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getCtmId(),
                customer.getCtmFirstName(),
                customer.getCtmLastName(),
                customer.getCtmEmail(),
                customer.getCtmPhone(),
                addressMapper.toDTO(customer.getAddress()),
                accountTypeResponseMapper.toResponseDTO(customer.getAccountType())
        );
    }

}
