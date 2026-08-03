package com.mns.cda.saas_facturation.product.Iservice;

import com.mns.cda.saas_facturation.product.DTO.MakerDTO;
import com.mns.cda.saas_facturation.product.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseMakerReferenceDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;

public interface IMakerReferenceService {

    List<MakerReferenceDTO> findAll();

    List<MakerDTO> findAllByArticle(Long artId);

    List<ArticleResponseMakerReferenceDTO> findAllByMaker(Long mkrId);

    MakerReferenceDTO findById(Long artId, Long mkrId) throws ResourceNotFoundException;

    MakerReferenceDTO create(MakerReferenceRequestDTO dto) throws ResourceNotFoundException;

    MakerReferenceDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws ResourceNotFoundException;

    void delete(Long artId, Long mkrId) throws ResourceNotFoundException;

}
