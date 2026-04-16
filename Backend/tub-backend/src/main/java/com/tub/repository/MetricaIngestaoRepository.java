package com.tub.repository;

import com.tub.model.MetricaIngestao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricaIngestaoRepository extends JpaRepository<MetricaIngestao, Long> {
}
