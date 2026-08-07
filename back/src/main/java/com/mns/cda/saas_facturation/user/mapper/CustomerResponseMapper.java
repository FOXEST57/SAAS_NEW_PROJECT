package com.mns.cda.saas_facturation.user.mapper;

import com.mns.cda.saas_facturation.user.DTO.responseDTO.CustomerResponseDTO;
import com.mns.cda.saas_facturation.location.mapper.AddressMapper;
import com.mns.cda.saas_facturation.user.model.Customer;
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
                customer.getAccountType().getAccTypeLibelle()
        );
    }

}
