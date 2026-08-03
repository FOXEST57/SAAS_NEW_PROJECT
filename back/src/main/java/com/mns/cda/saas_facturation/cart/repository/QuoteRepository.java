package com.mns.cda.saas_facturation.cart.repository;

import com.mns.cda.saas_facturation.cart.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

}