package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.SupplierReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierReferenceRepository extends JpaRepository<SupplierReference, SupplierReference.SupplierReferenceId> {

}
