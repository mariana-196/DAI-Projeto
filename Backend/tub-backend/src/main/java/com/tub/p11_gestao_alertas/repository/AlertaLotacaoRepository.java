package com.tub.p11_gestao_alertas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p11_gestao_alertas.model.AlertaLotacao;

@Repository
public interface AlertaLotacaoRepository extends JpaRepository<AlertaLotacao, Long> {
}