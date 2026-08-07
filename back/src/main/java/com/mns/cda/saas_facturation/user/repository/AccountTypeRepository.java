package com.mns.cda.saas_facturation.user.repository;

import com.mns.cda.saas_facturation.enumeration.AccountTypeEnum;
import com.mns.cda.saas_facturation.user.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {

    AccountType findAccountTypeByAccTypeLibelle(AccountTypeEnum accTypeLibelle);
}