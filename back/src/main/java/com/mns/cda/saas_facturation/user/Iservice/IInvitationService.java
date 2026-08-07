package com.mns.cda.saas_facturation.user.Iservice;

import com.mns.cda.saas_facturation.security.AppUserDetails;
import com.mns.cda.saas_facturation.user.DTO.InvitationDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.InvitationRequestDTO;

import java.util.List;

public interface IInvitationService {

    InvitationDTO create(AppUserDetails user,InvitationRequestDTO dto);

}
