package com.mns.cda.saas_facturation.service;

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
    public List<PostalCode> findAll() {
        return postalCodeRepository.findAll();
    }

    @Override
    public Optional<PostalCode> findById(Long pcodeId) {
        return postalCodeRepository.findById(pcodeId);
    }

    @Override
    public void create(PostalCode postalCode) {
        postalCode.setPcodeId(null);
        postalCodeRepository.save(postalCode);
    }

    @Override
    public void modify(Long pcodeId, PostalCode postalCode) throws PostalCodeNotFoundException {
        Optional<PostalCode> optionalPostalCode = postalCodeRepository.findById(pcodeId);

        if (optionalPostalCode.isEmpty()) {
            throw new PostalCodeNotFoundException();
        }

        postalCode.setPcodeId(pcodeId);
        postalCodeRepository.save(postalCode);
    }

    @Override
    public void delete(Long pcodeId) throws PostalCodeNotFoundException {
        Optional<PostalCode> optionalPostalCode = postalCodeRepository.findById(pcodeId);
        
        if (optionalPostalCode.isEmpty()) {
            throw new PostalCodeNotFoundException();
        }
        
        postalCodeRepository.deleteById(pcodeId);
    }

}
