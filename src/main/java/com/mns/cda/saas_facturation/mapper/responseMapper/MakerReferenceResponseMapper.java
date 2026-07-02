package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerReferenceResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierReferenceResponseDTO;
import com.mns.cda.saas_facturation.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MakerReferenceResponseMapper {

    protected final ArticleResponseMakerReferenceMapper articleResponseMakerReferenceMapper;
    protected final MakerResponseMapper makerResponseMapper;

    public MakerReferenceResponseDTO toResponseDto(MakerReference makerReference) {

        return new MakerReferenceResponseDTO(
                makerReference.getMkrRefId(),
                makerResponseMapper.toResponseDTO(makerReference.getMaker()),
                makerReference.getMkrRefReference()
        );
    }
}
