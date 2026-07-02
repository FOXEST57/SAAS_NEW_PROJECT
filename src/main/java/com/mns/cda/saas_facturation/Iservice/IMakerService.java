package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;

public interface IMakerService {

    List<MakerDTO> findAll();

    MakerDTO findById(Long id) throws ResourceNotFoundException;

    MakerDTO create(MakerRequestDTO dto);

    void delete(Long id) throws ResourceNotFoundException;

    MakerDTO modify(Long id, MakerRequestDTO dto) throws ResourceNotFoundException;
}
