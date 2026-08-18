package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.MakerReferenceResponseArticleDTO;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MakerReferenceResponseArticleMapper {

    private final MakerResponseMapper makerResponseMapper;
    private final DeliveryResponseMapper deliveryResponseMapper;

    public MakerReferenceResponseArticleDTO toResponseArticleDto(MakerReference makerReference) {
        return new MakerReferenceResponseArticleDTO(
                makerReference.getMkrRefId(),
                makerResponseMapper.toResponseDTO(makerReference.getMaker()),
                makerReference.getArtMkrReference(),
                makerReference.getDeliveries().stream().map(deliveryResponseMapper::toResponseDto).toList()
        );
    }

}
