package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.AccountType;
import com.mns.cda.saas_facturation.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
}