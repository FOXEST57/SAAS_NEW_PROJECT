package com.mns.cda.saas_facturation.user.Iservice;

import com.mns.cda.saas_facturation.user.DTO.CorporationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CorporationRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ICorporationService {
    List<CorporationDTO> findAll();

    Optional<CorporationDTO> findById(Long id);

    CorporationDTO create(CorporationRequestDTO corporationRequestDTO, Long ownerId);

    @Transactional
    void delete(Long id);

    CorporationDTO update(Long id, CorporationRequestDTO corporationRequestDTO);
}
