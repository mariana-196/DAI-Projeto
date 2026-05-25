package com.tub.p11_gestao_alertas.repository;

import com.tub.p11_gestao_alertas.model.AlertaOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaOperacionalRepository extends JpaRepository<AlertaOperacional, Long> {
}
