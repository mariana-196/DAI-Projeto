package com.tub.p5_lotacao.repository;

import com.tub.model.HistoricoLotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoLotacaoRepository extends JpaRepository<HistoricoLotacao, Long> {
}