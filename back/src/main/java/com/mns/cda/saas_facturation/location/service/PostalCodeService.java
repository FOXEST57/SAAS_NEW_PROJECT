package com.mns.cda.saas_facturation.location.service;

import com.mns.cda.saas_facturation.location.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.location.DTO.requestDTO.PostalCodeRequestDTO;
import com.mns.cda.saas_facturation.location.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.location.mapper.PostalCodeMapper;
import com.mns.cda.saas_facturation.location.model.PostalCode;
import com.mns.cda.saas_facturation.location.repository.PostalCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostalCodeService implements IPostalCodeService {

    private final PostalCodeRepository postalCodeRepository;
    private final PostalCodeMapper postalCodeMapper;

    @Override
    public List<PostalCodeDTO> findAll() {
        return postalCodeRepository.findAll()
                .stream()
                .map(postalCodeMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<PostalCodeDTO> findById(Long pCodeId) {
        return postalCodeRepository.findById(pCodeId).map(postalCodeMapper::toDTO);
    }

    @Override
    public PostalCodeDTO create(PostalCodeRequestDTO dto) {
        PostalCode postalCode = new PostalCode(
                null,
                dto.pCodeName()
        );

        return postalCodeMapper.toDTO(postalCodeRepository.save(postalCode));
    }

    @Override
    public PostalCodeDTO update(Long pCodeId, PostalCodeRequestDTO dto) throws ResourceNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(pCodeId).orElseThrow(() -> new ResourceNotFoundException("Code Postal non existant"));

        postalCode.setPCodeName(dto.pCodeName());

        return postalCodeMapper.toDTO(postalCodeRepository.save(postalCode));
    }

    @Override
    public void delete(Long pCodeId) throws ResourceNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(pCodeId).orElseThrow(() -> new ResourceNotFoundException("Code Postal non existant"));

        postalCodeRepository.delete(postalCode);
    }

}
