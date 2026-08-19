package com.mns.cda.saas_facturation.product.mapper.responseMapper;

import com.mns.cda.saas_facturation.product.DTO.responseDTO.DeliveryResponseDTO;
import com.mns.cda.saas_facturation.product.model.Delivery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeliveryResponseMapper {

    public DeliveryResponseDTO toResponseDto(Delivery delivery) {
        return new DeliveryResponseDTO(
                delivery.getDlvId(),
                delivery.getDlvQuantity(),
                delivery.getDlvBuyingPrice(),
                delivery.getDlvStatus(),
                delivery.getDlvCreatedDate(),
                delivery.getDlvUpdatedDate()
        );
    }

}
