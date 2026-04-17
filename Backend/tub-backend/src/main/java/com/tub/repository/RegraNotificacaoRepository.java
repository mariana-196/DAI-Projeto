package com.tub.repository;

import com.tub.model.RegraNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegraNotificacaoRepository extends JpaRepository<RegraNotificacao, Long> {
}