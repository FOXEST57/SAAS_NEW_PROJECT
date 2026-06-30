package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerReferenceResponseDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MakerReferenceResponseMapper {

    public MakerReferenceResponseDTO toResponseDto(MakerReference makerReference) {
        ArticleResponseMakerReferenceDTO articleResponseMakerReferenceDTO = new ArticleResponseMakerReferenceDTO(
                makerReference.getArticle().getArtId(),
                makerReference.getArticle().getArtName()
        );

        MakerResponseDTO makerResponseDTO = new MakerResponseDTO(
                makerReference.getMaker().getMkrId(),
                makerReference.getMaker().getMkrName()
        );

        return new MakerReferenceResponseDTO(
                articleResponseMakerReferenceDTO,
                makerResponseDTO,
                makerReference.getMkrRefReference()
        );
    }
}
