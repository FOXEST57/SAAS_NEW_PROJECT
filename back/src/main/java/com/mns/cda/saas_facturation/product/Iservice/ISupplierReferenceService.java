package com.mns.cda.saas_facturation.product.Iservice;

import com.mns.cda.saas_facturation.product.DTO.SupplierDTO;
import com.mns.cda.saas_facturation.product.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.product.DTO.responseDTO.ArticleResponseSupplierDTO;
import com.mns.cda.saas_facturation.product.DTO.updateDTO.UpdateSupplierReferenceDTO;
import com.mns.cda.saas_facturation.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ISupplierReferenceService {

    List<SupplierReferenceDTO> findAll();

    Optional<SupplierReferenceDTO> findById(Long articleId, Long supplierId);

    //Get By Id Article
    List<SupplierDTO> findByArticleId(Long articleId);

    //Get By ID Supplier
    List<ArticleResponseSupplierDTO> findBySupplierId(Long supplierId);

    SupplierReferenceDTO create(SupplierReferenceRequestDTO dto)
            throws ResourceNotFoundException;

    //PUT
    SupplierReferenceDTO update(Long artId, Long mkrId, UpdateSupplierReferenceDTO dto)
            throws ResourceNotFoundException;

    void deleteById (Long articleId, Long supplierId);
}

