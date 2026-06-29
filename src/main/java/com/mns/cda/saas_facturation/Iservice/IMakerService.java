package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.MakerDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.MakerResponseDTO;
import com.mns.cda.saas_facturation.model.Maker;

import java.util.List;

public interface IMakerService {

    List<MakerDTO> findAll();

    MakerDTO findById(Long id) throws MakerNotFoundException;

    MakerDTO create(MakerRequestDTO dto);

    void delete(Long id) throws MakerNotFoundException;

    MakerDTO modify(Long id, MakerRequestDTO dto) throws MakerNotFoundException;

    MakerDTO toDto(Maker maker);

    MakerResponseDTO toResponseDTO(Maker maker);

    public static class MakerNotFoundException extends Exception {}
}
