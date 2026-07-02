package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Order.OrderId > {
}
