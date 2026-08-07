package com.mns.cda.saas_facturation.user.DTO.requestDTO;

public record CreateUserFromInvitationDTO(
        String token,
        String firstName,
        String lastName,
        String password,
        String phone,
        Long addressId
) {
}
