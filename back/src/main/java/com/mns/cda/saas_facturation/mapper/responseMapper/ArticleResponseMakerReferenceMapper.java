package com.mns.cda.saas_facturation.mapper.responseMapper;

import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.SupplierReferenceResponseDTO;
import com.mns.cda.saas_facturation.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ArticleResponseMakerReferenceMapper {

    protected final SupplierReferenceResponseMapper supplierReferenceResponseMapper;

    public ArticleResponseMakerReferenceDTO toResponseDto(Article article) {

        List<SupplierReferenceResponseDTO> supplierReferenceResponseDTOList = article
                .getSuppliers()
                .stream()
                .map(supplierReferenceResponseMapper::toResponseDTO)
                .toList();

        return new ArticleResponseMakerReferenceDTO(
                article.getArtId(),
                article.getArtName(),
                article.getArtReference(),
                supplierReferenceResponseDTOList
        );
    }
}
