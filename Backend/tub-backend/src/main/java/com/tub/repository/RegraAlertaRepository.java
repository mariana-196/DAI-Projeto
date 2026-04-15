package com.tub.repository;

import com.tub.model.RegraAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegraAlertaRepository extends JpaRepository<RegraAlerta, Long> {
}