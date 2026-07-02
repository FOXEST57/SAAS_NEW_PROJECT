package com.mns.cda.saas_facturation.DTO;

public record GlobalExceptionInterceptorDTO(
        Integer status,
        String error,
        String message
) {
}
