package com.tub.p1_autenticacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p1_autenticacao.model.RegistoUtilizador;

import java.util.Optional;

@Repository
public interface RegistoUtilizadorRepository extends JpaRepository<RegistoUtilizador, Long> {
    Optional<RegistoUtilizador> findByEmail(String email);
}