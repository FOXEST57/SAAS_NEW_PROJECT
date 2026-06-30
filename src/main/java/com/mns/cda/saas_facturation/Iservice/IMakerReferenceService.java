package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerReferenceResponseDTO;
import com.mns.cda.saas_facturation.model.MakerReference;
import com.mns.cda.saas_facturation.repository.MakerReferenceRepository;

import java.util.List;

public interface IMakerReferenceService {

    public class MakerReferenceNotFoundException extends Exception {}

    List<MakerReferenceResponseDTO> findAll();

    List<MakerReferenceResponseDTO> findAllByArticle(Long artId);

    List<MakerReferenceResponseDTO> findAllByMaker(Long mkrId);

    MakerReferenceResponseDTO findById(Long artId, Long mkrId) throws MakerReferenceNotFoundException;

    MakerReferenceResponseDTO create(MakerReferenceRequestDTO dto) throws IArticleService.ArticleNotFoundException,
            IMakerService.MakerNotFoundException;

    MakerReferenceResponseDTO modify(Long artId, Long mkrId, UpdateMakerReferenceDTO dto) throws MakerReferenceNotFoundException;

    void delete(Long artId, Long mkrId) throws MakerReferenceNotFoundException;

    MakerReferenceResponseDTO toResponseDto(MakerReference makerReference);
}
