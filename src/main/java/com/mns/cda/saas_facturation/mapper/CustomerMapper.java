package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.mapper.responseMapper.AccountTypeResponseMapper;
import com.mns.cda.saas_facturation.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerMapper {

    private final AddressMapper addressMapper;
    private final AccountTypeResponseMapper accountTypeResponseMapper;

    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
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
