package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.TvaResponseDTO;
import com.mns.cda.saas_facturation.model.Tva;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TvaResponseMapper {


    public TvaResponseDTO toResponseDto(Tva tva) {
        // Mapping simple champ par champ — pas de calcul ni de relation à résoudre
        return new TvaResponseDTO(
                tva.getTvaId(),
                tva.getTvaName(),
                tva.getTvaTaux()
        );
    }
}
