package com.mns.cda.saas_facturation.user.Iservice;

import com.mns.cda.saas_facturation.user.DTO.AccountTypeDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.AccountTypeRequestDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;

public interface IAccountTypeService {

    List<AccountTypeDTO> findAll();

    AccountTypeDTO findById(Long id) throws ResourceNotFoundException;

    AccountTypeDTO create(AccountTypeRequestDTO dto);

    void delete(Long id) throws ResourceNotFoundException;

    AccountTypeDTO modify(Long id, AccountTypeRequestDTO dto) throws ResourceNotFoundException;
}
