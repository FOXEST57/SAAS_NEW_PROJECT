package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.model.Maker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MakerMapper {

    public MakerDTO toDto(Maker maker) {
        return new MakerDTO(
                maker.getMkrId(),
                maker.getMkrName()
        );
    }
}
