package com.mns.cda.saas_facturation.user.service;

import com.mns.cda.saas_facturation.user.DTO.InvitationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.InvitationRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.IInvitationService;
import com.mns.cda.saas_facturation.user.mapper.InvitationMapper;
import com.mns.cda.saas_facturation.user.model.Invitation;
import com.mns.cda.saas_facturation.user.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitationService implements IInvitationService {

    private final InvitationMapper invitationMapper;
    private final InvitationRepository invitationRepository;

    private static final int VALIDITY_DAYS = 7;

    @Override
    @Transactional
    public InvitationDTO create(InvitationRequestDTO dto) {

        Invitation invitation = new Invitation();
        invitation.setInvEmail(dto.email());
        invitation.setInvitationType(dto.type());

        invitation.setInvToken("Ceci est un token");

        invitation.setInvExpirationDate(LocalDateTime.now().plusDays(VALIDITY_DAYS));

        return invitationMapper.toDTO(invitationRepository.save(invitation));
    }

}
