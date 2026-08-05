package com.mns.cda.saas_facturation.cart.repository;

import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuoteLineRepository extends JpaRepository<QuoteLine, Long> {
    QuoteLine findByArticleRef(String artRef);

    QuoteLine findByQuote_QotIdAndArticleRef(Long qotId,String artRef);

    @Query(value = "SELECT qotln.qotLnQuantity FROM QuoteLine AS qotln " +
            "INNER JOIN Quote AS qot ON qotln.quote.qotId = qot.qotId " +
            "INNER JOIN Command as cmd ON qot.qotId = cmd.quote.qotId " +
            "WHERE cmd.cmdStatus = 'DELIVERED' " +
            "AND cmd.cmdModifiedDate >= :inventoryDate " +
            "AND qotln.articleRef = :articleRef")
    List<Integer> getSentQuantityByArticle(@Param("inventoryDate") LocalDateTime inventoryDate, @Param("articleRef") String articleRef);
}