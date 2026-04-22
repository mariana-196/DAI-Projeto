package com.tub.p5_lotacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p5_lotacao.model.HistoricoLotacao;

@Repository
public interface HistoricoLotacaoRepository extends JpaRepository<HistoricoLotacao, Long> {
}