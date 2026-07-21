package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.AccountTypeDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.model.AccountType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountTypeMapper {

    public AccountTypeDTO toDto(AccountType AccountType) {
        return new AccountTypeDTO(
                AccountType.getAccTypeId(),
                AccountType.getAccTypeLibelle()
        );
    }
}
