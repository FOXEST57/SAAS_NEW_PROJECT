package com.mns.cda.saas_facturation.user.mapper;

import com.mns.cda.saas_facturation.location.mapper.AddressMapper;
import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.user.mapper.responseMapper.AccountTypeResponseMapper;
import com.mns.cda.saas_facturation.user.model.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerMapper {

    private final AddressMapper addressMapper;
    private final AccountTypeResponseMapper accountTypeResponseMapper;
    private final CustomerResponseMapper customerResponseMapper;

    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
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
