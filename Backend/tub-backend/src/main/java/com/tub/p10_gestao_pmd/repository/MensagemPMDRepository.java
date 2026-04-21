package com.tub.p10_gestao_pmd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p10_gestao_pmd.model.MensagemPMD;

@Repository
public interface MensagemPMDRepository extends JpaRepository<MensagemPMD, Long> {
}