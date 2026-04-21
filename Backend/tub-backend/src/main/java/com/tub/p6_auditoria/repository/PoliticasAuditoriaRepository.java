package com.tub.p6_auditoria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p6_auditoria.model.PoliticasAuditoria;

@Repository
public interface PoliticasAuditoriaRepository extends JpaRepository<PoliticasAuditoria, Long> {
}