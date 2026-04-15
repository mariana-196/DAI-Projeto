package com.tub.repository;

import com.tub.model.AlertaLotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaLotacaoRepository extends JpaRepository<AlertaLotacao, Long> {
}