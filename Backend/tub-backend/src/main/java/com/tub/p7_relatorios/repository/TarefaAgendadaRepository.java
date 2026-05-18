package com.tub.p7_relatorios.repository;

import com.tub.p7_relatorios.model.TarefaAgendada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaAgendadaRepository extends JpaRepository<TarefaAgendada, Long> {
}