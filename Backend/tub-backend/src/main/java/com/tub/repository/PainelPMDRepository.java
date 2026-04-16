package com.tub.repository;

import com.tub.model.PainelPMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PainelPMDRepository extends JpaRepository<PainelPMD, Long> {
    Optional<PainelPMD> findByCodigo(String codigo);
}