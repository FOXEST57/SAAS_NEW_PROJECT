package com.mns.cda.saas_facturation.product.DTO.responseDTO;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryResponseDTO(
        Long dlvId,
        int dlvQuantity,
        BigDecimal dlvBuyingPrice,
        DeliveryStatus dlvStatus,
        LocalDateTime  dlvCreatedDate,
        LocalDateTime  dlvUpdatedDate
) {
}