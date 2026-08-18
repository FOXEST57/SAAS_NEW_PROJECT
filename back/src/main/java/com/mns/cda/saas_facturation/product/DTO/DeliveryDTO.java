package com.mns.cda.saas_facturation.product.DTO;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeliveryDTO(
        Long dlvId,
        int dlvQuantity,
        BigDecimal dlvBuyingPrice,
        DeliveryStatus dlvStatus,
        LocalDateTime  dlvCreatedDate,
        LocalDateTime  dlvUpdatedDate,
        SupplierReferenceResponseDeliveryDTO supplierReference,
        MakerReferenceResponseDeliveryDTO makerReference
) {
}