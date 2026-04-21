package com.tub.p10_gestao_pmd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p10_gestao_pmd.model.AgendamentoPMD;

@Repository
public interface AgendamentoPMDRepository extends JpaRepository<AgendamentoPMD, Long> {
}