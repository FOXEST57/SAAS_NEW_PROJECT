package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.MakerReferenceResponseDeliveryDTO;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MakerReferenceResponseDeliveryMapper {

    private final ArticleResponseMakerReferenceMapper articleResponseMakerReferenceMapper;
    private final MakerResponseMapper makerResponseMapper;

    public MakerReferenceResponseDeliveryDTO toResponseDeliveryDto(MakerReference makerReference) {
        return new MakerReferenceResponseDeliveryDTO(
                makerReference.getMkrRefId(),
                articleResponseMakerReferenceMapper.toResponseDto(makerReference.getArticle()),
                makerResponseMapper.toResponseDTO(makerReference.getMaker()),
                makerReference.getArtMkrReference()
        );
    }
}
