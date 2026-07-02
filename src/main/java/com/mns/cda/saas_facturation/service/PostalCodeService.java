package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;
import com.mns.cda.saas_facturation.mapper.PostalCodeMapper;
import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.repository.PostalCodeRepository;
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
    public Optional<PostalCodeDTO> findById(Long pcodeId) {
        return postalCodeRepository.findById(pcodeId).map(postalCodeMapper::toDTO);
    }

    @Override
    public PostalCodeDTO create(PostalCodeRequestDTO dto) {
        PostalCode postalCode = new PostalCode(
                null,
                dto.pcodeName()
        );

        return postalCodeMapper.toDTO(postalCodeRepository.save(postalCode));
    }

    @Override
    public PostalCodeDTO update(Long pcodeId, PostalCodeRequestDTO dto) throws ResourceNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(pcodeId).orElseThrow(() -> new ResourceNotFoundException("Code Postal non existant"));

        postalCode.setPcodeName(dto.pcodeName());

        return postalCodeMapper.toDTO(postalCodeRepository.save(postalCode));
    }

    @Override
    public void delete(Long pcodeId) throws ResourceNotFoundException {
        PostalCode postalCode = postalCodeRepository.findById(pcodeId).orElseThrow(() -> new ResourceNotFoundException("Code Postal non existant"));

        postalCodeRepository.delete(postalCode);
    }

}
