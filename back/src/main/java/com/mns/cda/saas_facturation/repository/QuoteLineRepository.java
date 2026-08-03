package com.mns.cda.saas_facturation.repository;

import com.mns.cda.saas_facturation.model.QuoteLine;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteLineRepository extends JpaRepository<QuoteLine, Long> {
    QuoteLine findByArticleRef(String artRef);

    QuoteLine findByQuote_QotIdAndArticleRef(Long qotId,String artRef);
}