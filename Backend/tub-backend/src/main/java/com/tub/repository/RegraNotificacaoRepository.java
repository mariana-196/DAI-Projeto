package com.tub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p6_auditoria.model.RegraNotificacao;

@Repository
public interface RegraNotificacaoRepository extends JpaRepository<RegraNotificacao, Long> {
}