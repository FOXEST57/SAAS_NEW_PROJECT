package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, OrderLine.OrderLineId> {

    //Retourne une liste de OrderLine par CrtId
    List<OrderLine> findByOrdLnId_CartId(Long cartId);

    List<OrderLine> findByOrdLnId_ArticleId(Long articleId);

}
