package com.mns.cda.saas_facturation.cart.DTO.patchDTO;

import com.mns.cda.saas_facturation.enumeration.CartStatus;
import jakarta.validation.constraints.NotNull;

public record PatchCartStatus(
        @NotNull Long crtId,
        @NotNull CartStatus crtStatus
) {
}
