package com.mns.cda.saas_facturation.DTO;

public record SupplierDTO(
        Long id,
        String name,
        String email,
        String phoneNumber,
        String address
) {
}
