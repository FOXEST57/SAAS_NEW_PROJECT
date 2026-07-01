package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.MakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;

import java.util.List;

public interface IMakerReferenceService {

    public class MakerReferenceNotFoundException extends Exception {}

    List<MakerReferenceDTO> findAll();

    List<MakerReferenceDTO> findAllByArticle(Long artId);

    List<MakerReferenceDTO> findAllByMaker(Long mkrId);

    MakerReferenceDTO findById(Long artId, Long mkrId) throws MakerReferenceNotFoundException;

    MakerReferenceDTO create(MakerReferenceRequestDTO dto) throws IArticleService.ArticleNotFoundException,
            IMakerService.MakerNotFoundException;

    MakerReferenceDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws MakerReferenceNotFoundException;

    void delete(Long artId, Long mkrId) throws MakerReferenceNotFoundException;

}
