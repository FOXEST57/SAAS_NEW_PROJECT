package com.mns.cda.saas_facturation.location.mapper;

import com.mns.cda.saas_facturation.location.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.location.model.PostalCode;
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
