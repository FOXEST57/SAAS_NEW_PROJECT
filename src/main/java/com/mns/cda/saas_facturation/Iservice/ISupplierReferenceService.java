package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.model.SupplierReference;

import java.util.List;
import java.util.Optional;

public interface ISupplierReferenceService {


    public static class SupplierReferenceNotFoundException extends Exception {}

    List<SupplierReferenceDTO> findAll();

    Optional<SupplierReferenceDTO> findById(SupplierReference.SupplierReferenceId id);

    //Get By Id Article
    List<SupplierReferenceDTO> findByArticleId(Long articleId);

    //Get By ID Supplier
    List<SupplierReferenceDTO> findBySupplierId(Long supplierId);

    SupplierReferenceDTO create(SupplierReferenceRequestDTO dto)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException;

    SupplierReferenceDTO update(SupplierReference.SupplierReferenceId id, SupplierReferenceRequestDTO dto)
            throws SupplierReferenceNotFoundException,
            IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException;

    void deleteById(SupplierReference.SupplierReferenceId id);
}
