package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.SupplierReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierReferenceRepository extends JpaRepository<SupplierReference, SupplierReference.SupplierReferenceId> {

    List<SupplierReference> findBySplRefId_ArticleId(Long splRefIdArticleId);

    List<SupplierReference> findBySplRefId_SupplierId(Long splRefIdSupplierId);
}
