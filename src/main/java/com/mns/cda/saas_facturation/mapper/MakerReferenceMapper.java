package com.mns.cda.saas_facturation.mapper;

import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierReferenceResponseDTO;
import com.mns.cda.saas_facturation.mapper.responseMapper.ArticleResponseMakerReferenceMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.MakerResponseMapper;
import com.mns.cda.saas_facturation.mapper.responseMapper.SupplierReferenceResponseMapper;
import com.mns.cda.saas_facturation.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MakerReferenceMapper {

    protected final ArticleResponseMakerReferenceMapper articleResponseMakerReferenceMapper;
    protected final MakerResponseMapper makerResponseMapper;

    public MakerReferenceDTO toDto(MakerReference makerReference) {

        return new MakerReferenceDTO(
                articleResponseMakerReferenceMapper.toResponseDto(makerReference.getArticle()),
                makerResponseMapper.toResponseDTO(makerReference.getMaker()),
                makerReference.getMkrRefReference()
        );
    }
}
