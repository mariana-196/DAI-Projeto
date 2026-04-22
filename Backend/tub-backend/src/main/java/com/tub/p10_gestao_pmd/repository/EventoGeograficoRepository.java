package com.tub.p10_gestao_pmd.repository;

import com.tub.p10_gestao_pmd.model.EventoGeografico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoGeograficoRepository extends JpaRepository<EventoGeografico, Long> {
    

}