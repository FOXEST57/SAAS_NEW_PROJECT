package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.model.Maker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MakerResponseMapper {

    public MakerResponseDTO toResponseDTO (Maker maker) {
        return new MakerResponseDTO(
                maker.getMkrId(),
                maker.getMkrName()
        );
    }
}
