package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseMakerReferenceDTO;

import java.util.List;

public interface IMakerReferenceService {

    public class MakerReferenceNotFoundException extends Exception {}

    List<MakerReferenceDTO> findAll();

    List<MakerDTO> findAllByArticle(Long artId);

    List<ArticleResponseMakerReferenceDTO> findAllByMaker(Long mkrId);

    MakerReferenceDTO findById(Long artId, Long mkrId) throws MakerReferenceNotFoundException;

    MakerReferenceDTO create(MakerReferenceRequestDTO dto) throws IArticleService.ArticleNotFoundException,
            IMakerService.MakerNotFoundException;

    MakerReferenceDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws MakerReferenceNotFoundException;

    void delete(Long artId, Long mkrId) throws MakerReferenceNotFoundException;

}
