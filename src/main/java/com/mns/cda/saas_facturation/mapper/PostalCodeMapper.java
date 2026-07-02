package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.model.PostalCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostalCodeMapper {

    public PostalCodeDTO toDTO(PostalCode postalCode) {
        return new PostalCodeDTO(
                postalCode.getPCodeId(),
                postalCode.getPCodeName()
        );
    }

}
