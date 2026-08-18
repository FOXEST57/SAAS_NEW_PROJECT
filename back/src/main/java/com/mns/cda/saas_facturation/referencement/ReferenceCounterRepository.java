package com.mns.cda.saas_facturation.referencement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReferenceCounterRepository extends JpaRepository<ReferenceCounter, Long> {
    Optional<ReferenceCounter> findByCorporation_CorpIdAndObjectType(Long corporationCorpId, ReferenceType objectType);

    Optional<ReferenceCounter> findByCorporationIsNullAndObjectType(ReferenceType type);
}
