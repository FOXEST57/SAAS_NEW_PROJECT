package com.mns.cda.saas_facturation.cart.repository;

import com.mns.cda.saas_facturation.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

}
