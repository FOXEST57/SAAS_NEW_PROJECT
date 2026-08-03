package com.mns.cda.saas_facturation.user.mapper;

import com.mns.cda.saas_facturation.user.DTO.AccountTypeDTO;
import com.mns.cda.saas_facturation.user.model.AccountType;
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
