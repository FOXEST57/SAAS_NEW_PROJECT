package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Category;
import com.mns.cda.saas_facturation.model.Tva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository <Category,Long> {
}
