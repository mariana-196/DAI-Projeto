package com.tub.repository;

import com.tub.model.PoliticasAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoliticasAuditoriaRepository extends JpaRepository<PoliticasAuditoria, Long> {
}