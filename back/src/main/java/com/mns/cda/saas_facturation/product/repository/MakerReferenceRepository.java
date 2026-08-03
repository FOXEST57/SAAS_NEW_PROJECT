package com.mns.cda.saas_facturation.product.repository;

import com.mns.cda.saas_facturation.product.model.MakerReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MakerReferenceRepository extends JpaRepository<MakerReference, MakerReference.MakerReferenceId> {

    List<MakerReference> findByMkrRefId_ArticleId(Long artMkrIdArticleId);

    List<MakerReference> findByMkrRefId_MakerId(Long artMkrIdMakerId);

    void deleteByMkrRefId_ArticleId(Long artMkrIdArticleId);
}
