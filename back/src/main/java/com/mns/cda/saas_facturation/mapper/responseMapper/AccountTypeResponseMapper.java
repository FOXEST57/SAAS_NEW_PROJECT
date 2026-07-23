package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.AccountTypeResponseDTO;
import com.mns.cda.saas_facturation.model.AccountType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountTypeResponseMapper {

    public AccountTypeResponseDTO toResponseDTO (AccountType AccountType) {
        return new AccountTypeResponseDTO(
                AccountType.getAccTypeLibelle()
        );
    }
}
