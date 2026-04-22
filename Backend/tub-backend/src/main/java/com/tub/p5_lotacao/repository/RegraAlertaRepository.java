package com.tub.p5_lotacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p5_lotacao.model.RegraAlerta;

@Repository
public interface RegraAlertaRepository extends JpaRepository<RegraAlerta, Long> {
}