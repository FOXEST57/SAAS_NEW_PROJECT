package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeRequestDTO;
import com.mns.cda.saas_facturation.model.PostalCode;

import java.util.List;
import java.util.Optional;

public interface IPostalCodeService {

    public static class PostalCodeNotFoundException extends Exception {};

    List<PostalCodeDTO> findAll();

    Optional<PostalCodeDTO> findById(Long pCodeId);

    PostalCodeDTO create(PostalCodeRequestDTO postalCode);

    PostalCodeDTO update(Long pCodeId, PostalCodeRequestDTO postalCode) throws PostalCodeNotFoundException;

    void delete(Long pCodeId) throws PostalCodeNotFoundException;

}
