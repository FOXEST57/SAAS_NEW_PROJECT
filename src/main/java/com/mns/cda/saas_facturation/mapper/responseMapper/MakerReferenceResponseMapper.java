package com.mns.cda.saas_facturation.mapper.responseMapper;

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

    protected final SupplierReferenceResponseMapper supplierReferenceResponseMapper;

    public MakerReferenceResponseDTO toResponseDto(MakerReference makerReference) {

        List<SupplierReferenceResponseDTO> supplierReferenceResponseDTOList =
                        makerReference.getArticle().getSuppliers()
                                .stream()
                                .map(supplierReferenceResponseMapper::toResponseDTO)
                                .toList();

        ArticleResponseMakerReferenceDTO articleResponseMakerReferenceDTO = new ArticleResponseMakerReferenceDTO(
                makerReference.getArticle().getArtId(),
                makerReference.getArticle().getArtName(),
                makerReference.getArticle().getArtReference(),
                supplierReferenceResponseDTOList
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
