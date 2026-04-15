package com.tub.repository;

import com.tub.model.RegistoAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistoAuditoriaRepository extends JpaRepository<RegistoAuditoria, Long> {
}