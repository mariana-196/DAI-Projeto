package com.tub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p6_auditoria.model.ConfiguracaoNotificacoes;

@Repository
public interface ConfiguracaoNotificacoesRepository extends JpaRepository<ConfiguracaoNotificacoes, Long> {
}