package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.product.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.ArticleResponseMakerReferenceMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.DeliveryResponseMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.MakerResponseMapper;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class MakerReferenceMapper {

    private final ArticleResponseMakerReferenceMapper articleResponseMakerReferenceMapper;
    private final MakerResponseMapper makerResponseMapper;
    private final DeliveryResponseMapper deliveryResponseMapper;

    public MakerReferenceDTO toDto(MakerReference makerReference) {

        return new MakerReferenceDTO(
                articleResponseMakerReferenceMapper.toResponseDto(makerReference.getArticle()),
                makerResponseMapper.toResponseDTO(makerReference.getMaker()),
                makerReference.getArtMkrReference(),
                makerReference.getDeliveries().stream().map(deliveryResponseMapper::toResponseDto).toList()
        );
    }

}
