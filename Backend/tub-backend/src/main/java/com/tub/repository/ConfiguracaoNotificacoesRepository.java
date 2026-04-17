package com.tub.repository;

import com.tub.model.ConfiguracaoNotificacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoNotificacoesRepository extends JpaRepository<ConfiguracaoNotificacoes, Long> {
}