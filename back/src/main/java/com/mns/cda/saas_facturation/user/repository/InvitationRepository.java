package com.mns.cda.saas_facturation.user.repository;

import com.mns.cda.saas_facturation.user.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long>  {

}
