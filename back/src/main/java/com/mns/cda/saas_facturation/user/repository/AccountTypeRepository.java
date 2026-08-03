package com.mns.cda.saas_facturation.user.repository;

import com.mns.cda.saas_facturation.user.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
}