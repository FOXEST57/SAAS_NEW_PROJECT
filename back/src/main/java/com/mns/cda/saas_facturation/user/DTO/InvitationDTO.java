package com.mns.cda.saas_facturation.user.DTO;

import com.mns.cda.saas_facturation.enumeration.InvitationTypeEnum;
import com.mns.cda.saas_facturation.user.model.Invitation;

import java.time.LocalDateTime;

public record InvitationDTO(
        Long invId,
        String invEmail,
        String customerLastName,
        LocalDateTime invExpirationDate,
        InvitationTypeEnum invType,
        String corpoName
) {

}
