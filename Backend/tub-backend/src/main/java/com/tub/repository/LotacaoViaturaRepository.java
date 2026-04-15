package com.tub.repository;

import com.tub.model.LotacaoViatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotacaoViaturaRepository extends JpaRepository<LotacaoViatura, Long> {
    Optional<LotacaoViatura> findByViaturaId(Integer viaturaId);
}