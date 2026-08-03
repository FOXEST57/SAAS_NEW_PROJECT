package com.mns.cda.saas_facturation.exception.DTO;

public record GlobalExceptionInterceptorDTO(
        Integer status,
        String error,
        String message
) {
}
