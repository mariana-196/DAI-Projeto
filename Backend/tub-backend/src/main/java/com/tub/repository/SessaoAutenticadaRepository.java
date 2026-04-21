package com.tub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p1_autenticacao.model.SessaoAutenticada;

import java.util.Optional;

@Repository
public interface SessaoAutenticadaRepository extends JpaRepository<SessaoAutenticada, Long> {
    Optional<SessaoAutenticada> findByToken(String token);
}
