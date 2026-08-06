package com.mns.cda.saas_facturation.user.mapper.responseMapper;

import com.mns.cda.saas_facturation.user.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.user.model.AccountType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountTypeResponseMapper {

    public AccountTypeResponseDTO toResponseDTO (AccountType AccountType) {
        return new AccountTypeResponseDTO(
                AccountType.getAccTypeLibelle().toString()
        );
    }
}
