package com.mns.cda.saas_facturation.cart.repository;

import com.mns.cda.saas_facturation.product.DTO.ActualStockDTO;
import com.mns.cda.saas_facturation.cart.model.QuoteLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuoteLineRepository extends JpaRepository<QuoteLine, Long> {
    @Query(value = """
   SELECT
       a.art_id AS artId,
       a.art_name AS artName,
       a.art_reference AS artRef,
       /* + MAKER PENDING */
       COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'PENDING'
       ), 0)
       /* + SUPPLIER PENDING */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'PENDING'
       ), 0)
   FROM article a;
""", nativeQuery = true)
    List<ActualStockDTO> getPendingStock();


    @Query(value = "SELECT qotln.qotLnQuantity FROM QuoteLine AS qotln " +
            "INNER JOIN Quote AS qot ON qotln.quote.qotId = qot.qotId " +
            "INNER JOIN Command AS cmd ON qot.qotId = cmd.quote.qotId " +
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
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
       /* + SUPPLIER RECEIVED */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
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


    @Query(value = """
    SELECT qotln.qotLnQuantity FROM QuoteLine AS qotln
    INNER JOIN Quote AS qot ON qotln.quote.qotId = qot.qotId
    INNER JOIN Command AS cmd ON qot.qotId = cmd.quote.qotId
    WHERE cmd.cmdStatus IN ('CREATED', 'PENDING', 'ACCEPTED')
    AND qotln.articleRef = :articleRef
    """)
    List<Integer> getOrderedQuantityByArticle(@Param("articleRef") String articleRef);

    @Query(value = """
   SELECT\s
       a.art_id AS artId,
       a.art_name AS artName,
       a.art_reference AS artRef,
       /* RESERVED */
       COALESCE((
           SELECT SUM(qotln.qot_ln_quantity)
           FROM quote_line qotln
           JOIN quote q ON qotln.quote_qot_id = q.qot_id
           JOIN command cmd ON cmd.quote_id = q.qot_id
           WHERE qotln.article_ref = a.art_reference
             AND cmd.cmd_status IN ('CREATED', 'PENDING', 'ACCEPTED')
       ), 0)
    FROM article a;
    """, nativeQuery = true)
    List<ActualStockDTO> getOrderedStock();


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
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
       /* + SUPPLIER RECEIVED */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
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
        
        /* - ORDERED */
        - COALESCE((
           SELECT SUM(qotln.qot_ln_quantity)
           FROM quote_line qotln
           JOIN quote q ON qotln.quote_qot_id = q.qot_id
           JOIN command cmd ON cmd.quote_id = q.qot_id
           WHERE qotln.article_ref = a.art_reference
             AND cmd.cmd_status IN ('CREATED', 'PENDING', 'ACCEPTED')
       ), 0)
   FROM article a;
""", nativeQuery = true)
    List<ActualStockDTO> getAvailableStock();


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
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
       /* + SUPPLIER RECEIVED */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
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
        
        /* + PENDING */
        /* + MAKER PENDING */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'PENDING'
       ), 0)
       /* + SUPPLIER PENDING */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'PENDING'
       ), 0)
   FROM article a;
""", nativeQuery = true)
    List<ActualStockDTO> getTheoreticalStock();


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
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
                   SELECT inv.inv_date
                   FROM inventory inv
                   WHERE inv.article_art_id = a.art_id
                   ORDER BY inv.inv_date DESC
                   LIMIT 1
             )
       ), 0)
   
       /* + SUPPLIER RECEIVED */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'RECEIVED'
             AND dlv.dlv_updated_date >= (
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
        
        /* + PENDING */
        /* + MAKER PENDING */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.mkr_article_id = a.art_id
             AND dlv.dlv_status = 'PENDING'
       ), 0)
       /* + SUPPLIER PENDING */
       + COALESCE((
           SELECT SUM(dlv.dlv_quantity)
           FROM delivery dlv
           WHERE dlv.spl_article_id = a.art_id
             AND dlv.dlv_status = 'PENDING'
       ), 0)
   
        /* - ORDERED */
        - COALESCE((
           SELECT SUM(qotln.qot_ln_quantity)
           FROM quote_line qotln
           JOIN quote q ON qotln.quote_qot_id = q.qot_id
           JOIN command cmd ON cmd.quote_id = q.qot_id
           WHERE qotln.article_ref = a.art_reference
             AND cmd.cmd_status IN ('CREATED', 'PENDING', 'ACCEPTED')
       ), 0)
   FROM article a;
""", nativeQuery = true)
    List<ActualStockDTO> getTotalStock();

}