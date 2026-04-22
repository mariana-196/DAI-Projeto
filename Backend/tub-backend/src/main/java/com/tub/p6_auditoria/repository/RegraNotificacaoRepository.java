package com.tub.p6_auditoria.repository;

import com.tub.p6_auditoria.model.RegraNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegraNotificacaoRepository extends JpaRepository<RegraNotificacao, Long> {
}