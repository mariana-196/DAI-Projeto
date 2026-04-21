package com.tub.p10_gestao_pmd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p10_gestao_pmd.model.PainelPMD;

import java.util.Optional;

@Repository
public interface PainelPMDRepository extends JpaRepository<PainelPMD, Long> {
    Optional<PainelPMD> findByCodigo(String codigo);
}