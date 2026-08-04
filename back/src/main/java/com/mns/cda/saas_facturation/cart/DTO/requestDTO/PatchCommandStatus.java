package com.mns.cda.saas_facturation.cart.DTO.requestDTO;

import com.mns.cda.saas_facturation.enumeration.CommandStatus;
import jakarta.validation.constraints.NotNull;

public record PatchCommandStatus(
        @NotNull Long cmdId,
        @NotNull CommandStatus cmdStatus
) {
}
