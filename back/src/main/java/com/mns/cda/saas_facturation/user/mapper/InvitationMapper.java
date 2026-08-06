package com.mns.cda.saas_facturation.user.mapper;

import com.mns.cda.saas_facturation.user.DTO.InvitationDTO;
import com.mns.cda.saas_facturation.user.model.Invitation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvitationMapper {

    private final CustomerMapper customerMapper;

    public InvitationDTO toDTO(Invitation invitation) {
        return new InvitationDTO(
                invitation.getInvId(),
                invitation.getInvEmail(),
                invitation.getInvCreationDate(),
                invitation.getInvExpirationDate(),
                invitation.getCustomer() != null ? customerMapper.toDTO(invitation.getCustomer()) : null
        );
    }
}
