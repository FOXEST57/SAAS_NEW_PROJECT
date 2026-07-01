package com.mns.cda.saas_facturation.Iservice;

import com.mns.cda.saas_facturation.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateSupplierReferenceDTO;
import com.mns.cda.saas_facturation.model.SupplierReference;

import java.util.List;
import java.util.Optional;

public interface ISupplierReferenceService {


    public static class SupplierReferenceNotFoundException extends Exception {}

    List<SupplierReferenceDTO> findAll();

    Optional<SupplierReferenceDTO> findById(SupplierReference.SupplierReferenceId id);

    //Get By Id Article
    List<SupplierDTO> findByArticleId(Long articleId);

    //Get By ID Supplier
    List<ArticleResponseSupplierDTO> findBySupplierId(Long supplierId);

    SupplierReferenceDTO create(SupplierReferenceRequestDTO dto)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException;

    //PUT
    SupplierReferenceDTO update(Long artId, Long mkrId, UpdateSupplierReferenceDTO dto)
            throws SupplierReferenceNotFoundException;

    void deleteById (SupplierReference.SupplierReferenceId id);
}

