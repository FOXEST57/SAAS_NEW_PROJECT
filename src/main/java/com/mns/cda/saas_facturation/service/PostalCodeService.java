package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
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

    @Override
    public List<PostalCodeDTO> findAll() {
        return postalCodeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public Optional<PostalCodeDTO> findById(Long pcodeId) {
        return postalCodeRepository.findById(pcodeId).map(this::toDTO);
    }

    @Override
    public void create(PostalCode postalCode) {
        postalCode.setPcodeId(null);
        postalCodeRepository.save(postalCode);
    }

    @Override
    public PostalCodeDTO modify(Long pcodeId, PostalCode postalCode) {
        postalCode.setPcodeId(pcodeId);

        return toDTO(postalCodeRepository.save(postalCode));
    }

    @Override
    public void delete(Long pcodeId) {
        postalCodeRepository.deleteById(pcodeId);
    }

    protected PostalCodeDTO toDTO(PostalCode postalCode) {
        return new PostalCodeDTO(
                postalCode.getPcodeName()
        );
    }

}
