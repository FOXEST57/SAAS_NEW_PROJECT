package com.mns.cda.saas_facturation.product.mapper;

import com.mns.cda.saas_facturation.product.DTO.DeliveryDTO;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.MakerReferenceResponseDeliveryMapper;
import com.mns.cda.saas_facturation.product.mapper.responseMapper.SupplierReferenceResponseDeliveryMapper;
import com.mns.cda.saas_facturation.product.model.Delivery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeliveryMapper {

    private final MakerReferenceResponseDeliveryMapper makerReferenceResponseDeliveryMapper;
    private final SupplierReferenceResponseDeliveryMapper supplierReferenceResponseDeliveryMapper;

    public DeliveryDTO toDto(Delivery delivery) {
        return new DeliveryDTO(
                delivery.getDlvId(),
                delivery.getDlvQuantity(),
                delivery.getDlvBuyingPrice(),
                delivery.getDlvStatus(),
                delivery.getDlvCreatedDate(),
                delivery.getDlvUpdatedDate(),
                delivery.getSupplierReference() != null
                        ? supplierReferenceResponseDeliveryMapper.toResponseDeliveryDTO(delivery.getSupplierReference())
                        : null,
                delivery.getMakerReference() != null
                        ? makerReferenceResponseDeliveryMapper.toResponseDeliveryDto(delivery.getMakerReference())
                        : null
        );
    }

}
