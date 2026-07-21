package com.mns.cda.saas_facturation.mapper.responseMapper;


import com.mns.cda.saas_facturation.DTO.responseDTO.MakerReferenceResponseDTO;
import com.mns.cda.saas_facturation.model.MakerReference;
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
