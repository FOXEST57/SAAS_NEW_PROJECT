package com.mns.cda.saas_facturation.user.mapper;

import com.mns.cda.saas_facturation.user.DTO.InvitationDTO;
import com.mns.cda.saas_facturation.user.model.Invitation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvitationMapper {

    public InvitationDTO toDTO(Invitation invitation, String customerLastName) {
        return new InvitationDTO(
                invitation.getInvId(),
                invitation.getInvEmail(),
                customerLastName,
                invitation.getInvExpirationDate(),
                invitation.getInvitationType(),
                invitation.getCorporation().getCorpName()
        );
    }
}
