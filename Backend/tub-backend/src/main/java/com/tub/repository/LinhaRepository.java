package com.tub.repository;

import com.tub.model.Linha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinhaRepository extends JpaRepository<Linha, Long> {
    Optional<Linha> findByCodigo(String codigo);
}