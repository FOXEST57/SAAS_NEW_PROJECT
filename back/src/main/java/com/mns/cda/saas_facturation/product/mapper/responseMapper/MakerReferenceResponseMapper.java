package com.mns.cda.saas_facturation.product.mapper.responseMapper;


import com.mns.cda.saas_facturation.product.DTO.responseDTO.MakerReferenceResponseDTO;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class MakerReferenceResponseMapper {

    protected final ArticleResponseMakerReferenceMapper articleResponseMakerReferenceMapper;
    protected final MakerResponseMapper makerResponseMapper;

    public MakerReferenceResponseDTO toResponseDto(MakerReference makerReference) {

        return new MakerReferenceResponseDTO(
                makerReference.getMkrRefId(),
                makerResponseMapper.toResponseDTO(makerReference.getMaker()),
                makerReference.getArtMkrReference(),
                makerReference.getArtMkrStock(),
                makerReference.getArtMkrSellPrice()
        );
    }
}
