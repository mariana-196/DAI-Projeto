package com.tub.p12_kpis_operacionais.repository;

import com.tub.p12_kpis_operacionais.model.RegistoPontualidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistoPontualidadeRepository extends JpaRepository<RegistoPontualidade, Long> {
}
