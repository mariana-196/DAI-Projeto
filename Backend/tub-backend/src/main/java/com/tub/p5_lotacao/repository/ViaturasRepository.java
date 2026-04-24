package com.tub.p5_lotacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p10_gestao_pmd.model.Viatura;

@Repository
public interface ViaturasRepository extends JpaRepository<Viatura, Long> {
}