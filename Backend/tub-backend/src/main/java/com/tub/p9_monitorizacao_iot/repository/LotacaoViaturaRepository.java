package com.tub.p9_monitorizacao_iot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p9_monitorizacao_iot.model.LotacaoViatura;

import java.util.Optional;

@Repository
public interface LotacaoViaturaRepository extends JpaRepository<LotacaoViatura, Long> {
    Optional<LotacaoViatura> findByViaturaId(Integer viaturaId);
}