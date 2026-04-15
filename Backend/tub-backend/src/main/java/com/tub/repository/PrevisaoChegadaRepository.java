package com.tub.repository;

import com.tub.model.PrevisaoChegada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrevisaoChegadaRepository extends JpaRepository<PrevisaoChegada, Long> {
}