package com.mns.cda.saas_facturation.user.service;

import com.mns.cda.saas_facturation.enumeration.AccountTypeEnum;
import com.mns.cda.saas_facturation.enumeration.InvitationTypeEnum;
import com.mns.cda.saas_facturation.security.AppUserDetails;
import com.mns.cda.saas_facturation.user.DTO.CustomerDTO;
import com.mns.cda.saas_facturation.user.DTO.InvitationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.InvitationRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.IInvitationService;
import com.mns.cda.saas_facturation.user.mapper.InvitationMapper;
import com.mns.cda.saas_facturation.user.model.AccountType;
import com.mns.cda.saas_facturation.user.model.Customer;
import com.mns.cda.saas_facturation.user.model.Invitation;
import com.mns.cda.saas_facturation.user.repository.CustomerRepository;
import com.mns.cda.saas_facturation.user.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvitationService implements IInvitationService {

    private final InvitationMapper invitationMapper;
    private final InvitationRepository invitationRepository;

    private static final int VALIDITY_DAYS = 7;
    private final CustomerRepository customerRepository;

    public InvitationDTO validate(String token) {

        Invitation invitation = invitationRepository.findByInvToken(token);

        if (invitation.getInvExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invitation expirée");
        }
        if (invitation.isUsed()) {
            throw new IllegalStateException("Invitation déjà utilisée");
        }

        return invitationMapper.toDTO(invitation, invitation.getInvEmail());
    }


    @Override
    @Transactional
    public InvitationDTO create(AppUserDetails user, InvitationRequestDTO dto) {

        String Url = "http://localhost:4200/invite?token=";

        Customer customer = customerRepository.findById(user.getUser().getCtmId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Invitation invitation = new Invitation();

        if (customer.getAccountType().getAccTypeLibelle() == AccountTypeEnum.OWNER) {
            invitation.setInvitationType(InvitationTypeEnum.EMPLOYEE);

        } else if (customer.getAccountType().getAccTypeLibelle() == AccountTypeEnum.EMPLOYEE) {
            invitation.setInvitationType(InvitationTypeEnum.CUSTOMER);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        invitation.setInvToken(token);
        invitation.setInvEmail(dto.email());
        invitation.setInvExpirationDate(LocalDateTime.now().plusDays(VALIDITY_DAYS));
        invitation.setCorporation(customer.getCorporation());

        String invitationUrl = Url + token;

        log.warn("DEV MODE - Invitation URL: " + invitationUrl);

        return invitationMapper.toDTO(invitationRepository.save(invitation), dto.customerLastName());
    }

}
