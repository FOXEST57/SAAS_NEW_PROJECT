package com.mns.cda.saas_facturation.user.service;

import com.mns.cda.saas_facturation.user.DTO.AccountTypeDTO;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.AccountTypeRequestDTO;
import com.mns.cda.saas_facturation.user.Iservice.IAccountTypeService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.user.mapper.AccountTypeMapper;
import com.mns.cda.saas_facturation.user.model.AccountType;
import com.mns.cda.saas_facturation.user.repository.AccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTypeService implements IAccountTypeService {

    private final AccountTypeRepository AccountTypeRepository;
    private final AccountTypeMapper AccountTypeMapper;

    @Override
    public List<AccountTypeDTO> findAll() {
        return AccountTypeRepository.findAll()
                .stream()
                .map(AccountTypeMapper::toDto)
                .toList();
    }

    @Override
    public AccountTypeDTO findById(Long id) throws ResourceNotFoundException {
        return AccountTypeMapper.toDto(AccountTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de compte non existant")));
    }

    @Override
    public AccountTypeDTO create(AccountTypeRequestDTO dto) {
        AccountType AccountType = new AccountType(
                null,
                dto.accTypeLibelle()
        );

        return AccountTypeMapper.toDto(AccountTypeRepository.save(AccountType));
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {

        AccountType AccountType = AccountTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"));

        AccountTypeRepository.delete(AccountType);
    }

    @Override
    public AccountTypeDTO modify(Long id, AccountTypeRequestDTO dto) throws ResourceNotFoundException {

        AccountType AccountType = AccountTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fabricant non existant"));

        AccountType.setAccTypeLibelle(dto.accTypeLibelle());

        return AccountTypeMapper.toDto(AccountTypeRepository.save(AccountType));
    }
}
