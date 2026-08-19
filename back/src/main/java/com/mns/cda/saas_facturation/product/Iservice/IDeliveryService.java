package com.mns.cda.saas_facturation.product.Iservice;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.product.DTO.DeliveryDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.DeliveryRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.UpdateDeliveryDTO;

import java.util.List;

public interface IDeliveryService {

    List<DeliveryDTO> findAll();

    DeliveryDTO findById(Long dlvId) throws ResourceNotFoundException;

    DeliveryDTO create(DeliveryRequestDTO dto) throws ResourceNotFoundException;

    DeliveryDTO modify(Long dlvId, UpdateDeliveryDTO dto) throws ResourceNotFoundException;

    DeliveryDTO modifyStatus(Long dlvId, DeliveryStatus status);

    void delete(Long dlvId) throws ResourceNotFoundException;

}
