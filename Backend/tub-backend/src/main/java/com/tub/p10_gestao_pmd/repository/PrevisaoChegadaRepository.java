package com.tub.p10_gestao_pmd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p10_gestao_pmd.model.PrevisaoChegada;

@Repository
public interface PrevisaoChegadaRepository extends JpaRepository<PrevisaoChegada, Long> {
}