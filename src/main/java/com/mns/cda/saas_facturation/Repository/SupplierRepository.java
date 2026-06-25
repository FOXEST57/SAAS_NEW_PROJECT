package com.mns.cda.saas_facturation.Repository;

import com.mns.cda.saas_facturation.Model.SupplierModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierModel, Long> {

    boolean existsBySplName (String splName);

    boolean existsBySplNameAndSplIdIsNot(String splName, Long id);

}
