package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}
