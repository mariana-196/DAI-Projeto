package com.tub.repository;

import com.tub.model.SessaoAutenticada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessaoAutenticadaRepository extends JpaRepository<SessaoAutenticada, Long> {
    Optional<SessaoAutenticada> findByToken(String token);
}
