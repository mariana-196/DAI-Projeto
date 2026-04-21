package com.tub.p8_gestao_bilhetica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tub.p8_gestao_bilhetica.model.MetricaIngestao;

@Repository
public interface MetricaIngestaoRepository extends JpaRepository<MetricaIngestao, Long> {
}
