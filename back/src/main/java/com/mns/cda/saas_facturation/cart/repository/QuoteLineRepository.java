package com.mns.cda.saas_facturation.cart.repository;

import com.mns.cda.saas_facturation.cart.DTO.ActualStockDTO;
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

    @Query(value = """
   SELECT\s
       a.art_id AS artId,
       a.art_name AS artName,
       a.art_reference AS artRef,
   
       /* INVENTORY */
       COALESCE((
           SELECT inv.inv_stock
           FROM inventory inv
           WHERE inv.article_art_id = a.art_id
           ORDER BY inv.inv_date DESC
           LIMIT 1
       ), 0)
   
       /* + MAKER RECEIVED */
       + COALESCE((
           SELECT SUM(mk.art_mkr_stock)
           FROM maker_reference mk
           WHERE mk.article_id = a.art_id
             AND mk.status = 'RECEIVED'
             AND mk.art_mkr_update_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
       /* + SUPPLIER RECEIVED */
       + COALESCE((
           SELECT SUM(spl.spl_ref_stock)
           FROM supplier_reference spl
           WHERE spl.article_id = a.art_id
             AND spl.status = 'RECEIVED'
             AND spl.spl_ref_update_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
       /* - DELIVERED */
       - COALESCE((
           SELECT SUM(qotln.qot_ln_quantity)
           FROM quote_line qotln
           JOIN quote q ON qotln.quote_qot_id = q.qot_id
           JOIN command cmd ON cmd.quote_id = q.qot_id
           WHERE qotln.article_ref = a.art_reference
             AND cmd.cmd_status = 'DELIVERED'
             AND cmd.cmd_modified_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
   FROM article a;
""", nativeQuery = true)
    List<ActualStockDTO> getActualStock();


}