package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.InventoryDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.InventoryRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IInventoryService {

    List<InventoryDTO> findAll();

    Optional<InventoryDTO> findById(Long invId);

    public List<InventoryDTO> findByArticleId(Long articleId);

    InventoryDTO create(InventoryRequestDTO dto) throws ResourceNotFoundException;

    InventoryDTO update(Long invId, InventoryRequestDTO dto) throws ResourceNotFoundException;

    void delete(Long invId) throws ResourceNotFoundException;

}
