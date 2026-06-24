package com.mns.cda.saas_facturation.service;

import com.mns.cda.saas_facturation.model.PostalCode;

import java.util.List;
import java.util.Optional;

public interface IPostalCodeService {

    public static class PostalCodeNotFoundException extends Exception {};

    List<PostalCode> findAll();

    Optional<PostalCode> findById(Long pcodeId);

    void create(PostalCode postalCode);

    void modify(Long pcodeId, PostalCode postalCode) throws PostalCodeNotFoundException;

    void delete(Long pcodeId) throws PostalCodeNotFoundException;
}
